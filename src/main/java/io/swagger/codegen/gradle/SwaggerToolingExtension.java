package io.swagger.codegen.gradle;

public class SwaggerToolingExtension {

    private String codegenVersion = "2.2.2";

    public void setCodegenVersion(String codegenVersion) {
        this.codegenVersion = codegenVersion;
    }

    public String getCodegenVersion() {
        return codegenVersion;
    }
}
