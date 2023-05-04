package com.iaspec.uniongatewayserver.model;

/**
 * @author Flamenco.xxx
 * @date 2023/4/21  11:26
 */
public enum ExitCodeEnum {
    NORMAL_EXIT(0),

    FORCE_EXIT(1)
    ;

    private final int exitCode;


    ExitCodeEnum(int exitCode){
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return this.exitCode;
    }
}
