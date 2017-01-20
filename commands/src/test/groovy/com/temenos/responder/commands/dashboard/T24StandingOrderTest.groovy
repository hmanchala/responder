package com.temenos.responder.commands.dashboard

import com.temenos.responder.context.CommandContext
import com.temenos.responder.entity.runtime.Entity
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by aburgos on 19/01/2017.
 */
class T24StandingOrderTest extends Specification {

    @Unroll
    def "T24 customer information command"(id, map) {
        setup:
            def command = new T24StandingOrder()
            def context = Mock(CommandContext)
        when:
            command.execute(context)
        then:
            _ * context.getAttribute('standingOrderId') >> id
            _ * context.getAttribute('into') >> 'finalResult'
            1 * context.setAttribute('finalResult', new Entity(map))
            1 * context.setResponseCode('200')
        where:
            id   | map
            200  | ['ID': 200, 'TARGET': 'GB91 BKEN 1000 0041 6100 08', 'AMOUNT': 1200]
            400  | ['ID': 400, 'TARGET': 'GB27 BOFI 9021 2729 8235 29', 'AMOUNT': 2020]
            401  | ['ID': 401, 'TARGET': 'GB29 NWBK 6016 1331 9268 19', 'AMOUNT': 2000]
            402  | ['ID': 402, 'TARGET': 'GB29 NWBK 6016 1331 9268 53', 'AMOUNT': 4000]
    }

    @Unroll
    def "T24 customer information command for nonexistent customers"(id) {
        setup:
        def command = new T24StandingOrder()
        def context = Mock(CommandContext)
        when:
        command.execute(context)
        then:
        _ * context.getAttribute('standingOrderId') >> id
        _ * context.getAttribute('into') >> 'finalResult'
        1 * context.setAttribute('finalResult', new Entity())
        1 * context.setResponseCode('200')
        where:
        id << [666666, 999999]
    }
}