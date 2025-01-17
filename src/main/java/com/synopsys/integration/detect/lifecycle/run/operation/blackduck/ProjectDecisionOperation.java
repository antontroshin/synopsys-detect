/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.List;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class ProjectDecisionOperation {
    private static final String OPERATION_NAME = "Detect Project Decision";
    private final ProjectNameVersionDecider projectNameVersionDecider;
    private final OperationSystem operationSystem;
    private final List<DetectTool> preferredDetectTools;

    public ProjectDecisionOperation(ProjectNameVersionDecider projectNameVersionDecider, OperationSystem operationSystem, final List<DetectTool> preferredDetectTools) {
        this.projectNameVersionDecider = projectNameVersionDecider;
        this.operationSystem = operationSystem;
        this.preferredDetectTools = preferredDetectTools;
    }

    public NameVersion execute(List<DetectToolProjectInfo> detectToolProjectInfoList) throws DetectUserFriendlyException, IntegrationException {
        NameVersion projectNameVersion = projectNameVersionDecider.decideProjectNameVersion(preferredDetectTools, detectToolProjectInfoList);
        operationSystem.completeWithSuccess(OPERATION_NAME);
        return projectNameVersion;
    }
}
