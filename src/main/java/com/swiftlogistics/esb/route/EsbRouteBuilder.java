package com.swiftlogistics.esb.route;

import com.swiftlogistics.esb.model.DeliveryOrder;
import com.swiftlogistics.esb.processor.CmsSoapToCanonicalProcessor;
import com.swiftlogistics.esb.processor.WmsTcpParserProcessor;
import com.swiftlogistics.esb.processor.ValidationProcessor;
import com.swiftlogistics.esb.processor.EnrichmentProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EsbRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        errorHandler(defaultErrorHandler().maximumRedeliveries(2).redeliveryDelay(2000));

        // REST simulator endpoint to accept CMS SOAP XML POSTs (for demo)
        from("rest:post:/cms/receive")
            .routeId("cms-rest-simulator")
            .log("[CMS] Received body: ${body}")
            .process(new CmsSoapToCanonicalProcessor())
            .choice()
                .when(header("valid").isEqualTo(true)).to("direct:preQueueProcessing")
                .otherwise().to("direct:invalid")
            .end();

        // Netty TCP listener for WMS (port 5003)
        from("netty4:tcp://0.0.0.0:5003?textline=true&sync=false")
            .routeId("wms-tcp-adapter")
            .log("[WMS] Raw: ${body}")
            .process(new WmsTcpParserProcessor())
            .to("direct:preQueueProcessing")
        ;

        // Pre-queue processing: validate, enrich (call ROS), publish to RabbitMQ
        from("direct:preQueueProcessing")
            .routeId("preQueueProcessing")
            .log("[ESB] Pre-queue: ${body}")
            .process(new ValidationProcessor())
            .choice()
                .when(header("valid").isEqualTo(true))
                    .process(new EnrichmentProcessor())
                    .marshal().json()
                    .to("rabbitmq://localhost/exchange.orders?queue=orders.queue&routingKey=order.created")
                    .log("[ESB] Published to RabbitMQ: ${body}")
                .otherwise()
                    .to("direct:invalid")
            .end()
        ;

        from("direct:invalid")
            .routeId("invalid-handler")
            .log("[ESB] Invalid message: ${body} headers: ${headers}")
            .to("rabbitmq://localhost/exchange.orders?queue=orders.invalid")
        ;
    }
}
