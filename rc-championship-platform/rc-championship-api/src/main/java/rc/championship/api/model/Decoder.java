package rc.championship.api.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import rc.championship.api.services.decoder.DecoderConnectionFactory;
import rc.championship.api.services.decoder.DecoderConnector;
import rc.championship.api.services.decoder.DecoderListener;
import rc.championship.api.services.decoder.DecoderMessage;

/**
 *
 * @author Stefan
 */
public class Decoder {

    public static final String PROP_HOST = "host";
    public static final String PROP_PORT = "port";
    public static final String PROP_DECODER_NAME = "decoderName";

    private String host;
    private Integer port;
    private Integer id;
    private String decoderName;
    private Optional<DecoderConnectionFactory> connectorFactory;
    private String identifyer;
    private DecoderConnector connector;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Decoder(String host, int port, String decoderName, Optional<DecoderConnectionFactory> connectorFactory, String identifyer) {
        this.host = host;
        this.port = port;
        this.decoderName = decoderName;
        this.connectorFactory = connectorFactory;
        this.identifyer = identifyer;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        String old = this.host;
        this.host = host;
        if (old != null && !old.equalsIgnoreCase(host)) {
            pcs.firePropertyChange(PROP_HOST, old, host);
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        Integer old = this.port;
        this.port = port;
        if (old != port) {
            pcs.firePropertyChange(PROP_PORT, old, port);
        }
    }

    public String getDecoderName() {
        return decoderName;
    }

    public void setDecoderName(String decoderName) {
        String old = this.decoderName;
        this.decoderName = decoderName;
        if (old != null && !old.equalsIgnoreCase(decoderName)) {
            pcs.firePropertyChange(PROP_DECODER_NAME, old, decoderName);
        }
    }

    public Optional<DecoderConnectionFactory> getConnectorFactory() {
        return connectorFactory;
    }

    public void setConnectorFactory(Optional<DecoderConnectionFactory> connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    public String getIdentifyer() {
        return identifyer;
    }

    public void setIdentifyer(String identifyer) {
        this.identifyer = identifyer;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.identifyer);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Decoder other = (Decoder) obj;
        if (!Objects.equals(this.identifyer, other.identifyer)) {
            return false;
        }
        return true;
    }

    public void register(DecoderListener listener) {
        createConnectorIfNeeded();
        connector.register(listener);
    }

    public void unregister(DecoderListener listener) {
        createConnectorIfNeeded();
        connector.unregister(listener);
    }

    public void connect() throws IOException {
        createConnectorIfNeeded();
        if (connector.isConnected()) {
            return;
        }
        connector.connect();

    }

    private void createConnectorIfNeeded() throws IllegalStateException {
        if (connector == null) {

            if (connectorFactory == null || !connectorFactory.isPresent()) {
                throw new IllegalStateException("Unknown decoder implementation: " + decoderName);
            }
            connector = connectorFactory.get().createConnector(this);
            if (connector == null) {
                throw new IllegalStateException("Can not connect to decoder, no valid connector was found for " + decoderName);
            }
        }

    }

    public boolean isConnected() {
        return connector != null ? connector.isConnected() : false;
    }

    public void disconnect() {
        if (connector != null) {
            connector.disconnect();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s:%d", decoderName, host, port);
    }

    public void send(DecoderMessage msg, long timeout, TimeUnit timeUnit) throws IOException, InterruptedException {
        if(!isConnected()){
            throw new IllegalStateException("Can not send message, decoder has no active connection");
        }
        connector.send(msg, timeout, timeUnit);
    }

    
}
