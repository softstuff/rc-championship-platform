package rc.championship.api.services.decoder;

/**
 *
 * @author Stefan
 */
public final class DecoderRecorderFilter {
    private boolean include;
    
    private String prefix;
    private String suffix;

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    
    
}
