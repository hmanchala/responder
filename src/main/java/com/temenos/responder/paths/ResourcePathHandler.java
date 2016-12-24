package com.temenos.responder.paths;

import com.google.inject.Inject;
import com.temenos.responder.configuration.Resource;
import com.temenos.responder.context.Parameters;
import com.temenos.responder.exception.ResourceNotFoundException;
import com.temenos.responder.loader.ScriptLoader;
import com.temenos.responder.mapper.ResourceMapper;
import com.temenos.responder.producer.Producer;

import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Douglas Groves on 09/12/2016.
 */
public class ResourcePathHandler implements PathHandler {
    private final List<Resource> resources;
    private static final String NOT_FOUND_MSG = "No resource could be resolved for path: %s";

    @Inject
    public ResourcePathHandler(ScriptLoader loader, Producer producer, ResourceMapper mapper){
        this.resources = loadCoreResources(loader, producer, mapper);
    }

    public ResourcePathHandler(List<Resource> resources){
        this.resources = resources;
    }

    private List<Resource> loadCoreResources(ScriptLoader loader, Producer producer, ResourceMapper mapper) {
        try {
            List<String> resources = new ArrayList<>(loader.loadAll().values());
            List<Object> deserialisedJson = new ArrayList<>();
            for(String json : resources){
                deserialisedJson.add(producer.deserialise(json));
            }
            List<?> mappedResources = (List<?>) mapper.mapAll(deserialisedJson);
            List<Resource> cleanResources = new ArrayList<>();
            for (Object o : mappedResources) {
                cleanResources.add(Resource.class.cast(o));
            }
            return cleanResources;
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load resources", ioe);
        }
    }


    @Override
    public Resource resolvePathSpecification(String path, String method) throws ResourceNotFoundException {
        for(Resource r : resources){
            if((r.getPathSpec().equals(path) || pathMatchesSpec(path, r.getPathSpec())) && r.getHttpMethod().equals(method)){
                return r;
            }
        }
        throw new ResourceNotFoundException(String.format(NOT_FOUND_MSG, path),
                Response.status(Response.Status.NOT_FOUND).entity("{\"Message\":\"Not Found\", \"code\": 404}").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build());
    }

    @Override
    public Parameters resolvePathParameters(String path, Resource resource) {
        Parameters parameters = new Parameters();
        String[] pathSegments = path.split("/"), specSegments = resource.getPathSpec().split("/");
        for(int i = 0; i < pathSegments.length; i++) {
            if (specSegments[i].matches("\\{.+?\\}")) {
                // remove brackets
                String param = specSegments[i].substring(1, specSegments[i].length()-1);
                parameters.setValue(param, pathSegments[i]);
            }
        }

        return parameters;
    }

    private boolean pathMatchesSpec(String path, String spec){
        String[] pathSegments = path.split("/"), specSegments = spec.split("/");
        if(pathSegments.length != specSegments.length){
            return false;
        }
        for(int i = 0; i < pathSegments.length; i++){
            if(specSegments[i].matches("\\{.+?\\}") || pathSegments[i].equals(specSegments[i])){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }
}
