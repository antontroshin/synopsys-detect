/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow.report.output;

import java.util.List;

import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detector.base.DetectorType;

public class FormattedIssueOutput {
    public final DetectTool tool;
    public final DetectorType detectorType;
    public final String type;
    public final String id;
    public final List<String> messages;

    public FormattedIssueOutput(DetectTool tool, DetectorType detectorType, String type, String id, List<String> messages) {
        this.tool = tool;
        this.detectorType = detectorType;
        this.type = type;
        this.id = id;
        this.messages = messages;
    }
}
