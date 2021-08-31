module org.dumin {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.rmi;
    requires java.base;
    requires org.apache.commons.codec;
    requires org.apache.commons.io;
    requires zip4j;

    opens org.dumin to javafx.fxml;
    exports org.dumin;

    opens model to com.google.gson;


}