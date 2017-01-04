package com.temenos.responder.flows

import com.temenos.responder.commands.Command
import com.temenos.responder.commands.VersionInformation
import com.temenos.responder.context.CommandContext
import com.temenos.responder.context.DefaultCommandContext
import com.temenos.responder.context.ExecutionContext

/**
 * This flow reads a generated JSON file and forwards its contents to the response.
 *
 * Created by Douglas Groves on 04/01/2017.
 */
class VersionInformationFlow implements Flow {

    @Override
    def execute(ExecutionContext context) {
        //fetch command
        Command command = context.getCommand(VersionInformation.class)

        //create command-scoped context
        CommandContext ctx = new DefaultCommandContext()

        //set 'from' attribute
        ctx.setAttribute("from", ["versionInfo.json"])

        //set 'into' attribute
        ctx.setAttribute("into", "finalResult")


        //execute the command
        command.execute(ctx)

        //pass command output back to execution context
        context.setAttribute("finalResult", ctx.getAttribute("finalResult"))

        //set response code to 200 OK
        context.setResponseCode(ctx.getResponseCode())
    }
}
