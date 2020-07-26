package io.github.hodev.dbrepair;

public class RepairConfig {
    /** Name of version to use current one. */
    public final static String CURRENT_VERSION = "current";

    private String tempDirectory = "/tmp";
    private String targetDbVersion = "4.0.0";
    private String inputDbLocation;
    private String outputDbLocation;
    private String archiveLocation;

    public String getTempDirectory() {
        return tempDirectory;
    }

    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    public String getTargetDbVersion() {
        return targetDbVersion;
    }

    public void setTargetDbVersion(String targetDbVersion) {
        this.targetDbVersion = targetDbVersion;
    }

    public boolean reuseSameVersion() {
        return CURRENT_VERSION.equalsIgnoreCase(targetDbVersion);
    }

    public String getInputDbLocation() {
        return inputDbLocation;
    }

    public void setInputDbLocation(String inputDbLocation) {
        this.inputDbLocation = inputDbLocation;
    }

    public String getOutputDbLocation() {
        return outputDbLocation;
    }

    public void setOutputDbLocation(String outputDbLocation) {
        this.outputDbLocation = outputDbLocation;
    }

    public String getArchiveLocation() {
        return archiveLocation;
    }

    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }
}
