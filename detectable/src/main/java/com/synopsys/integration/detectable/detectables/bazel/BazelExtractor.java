/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bazel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionFullRule;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionFullRuleJsonProcessor;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalIdExtractionSimpleRules;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelCodeLocationBuilder;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelExternalIdGenerator;
import com.synopsys.integration.detectable.detectables.bazel.parse.dependencydetail.ArtifactStringsExtractorTextProto;
import com.synopsys.integration.detectable.detectables.bazel.parse.dependencydetail.BazelQueryTextProtoOutputParser;
import com.synopsys.integration.detectable.detectables.bazel.parse.dependencydetail.BazelQueryXmlOutputParser;
import com.synopsys.integration.detectable.detectables.bazel.parse.RuleConverter;
import com.synopsys.integration.detectable.detectables.bazel.parse.dependencydetail.ArtifactStringsExtractor;
import com.synopsys.integration.detectable.detectables.bazel.parse.dependencydetail.ArtifactStringsExtractorXml;
import com.synopsys.integration.detectable.detectables.bazel.parse.dependencydetail.BazelDetailsQueryExecutor;

public class BazelExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final BazelDetailsQueryExecutor bazelDetailsQueryExecutor;
    private final BazelQueryXmlOutputParser xmlParser;
    private final BazelQueryTextProtoOutputParser textProtoParser;
    private final BazelCodeLocationBuilder codeLocationGenerator;
    private final BazelExternalIdExtractionFullRuleJsonProcessor bazelExternalIdExtractionFullRuleJsonProcessor;

    public BazelExtractor(final ExecutableRunner executableRunner, final BazelDetailsQueryExecutor bazelDetailsQueryExecutor,
        final BazelQueryXmlOutputParser xmlParser,
        final BazelQueryTextProtoOutputParser textProtoParser,
        final BazelCodeLocationBuilder codeLocationGenerator, final BazelExternalIdExtractionFullRuleJsonProcessor bazelExternalIdExtractionFullRuleJsonProcessor) {
        this.executableRunner = executableRunner;
        this.bazelDetailsQueryExecutor = bazelDetailsQueryExecutor;
        this.xmlParser = xmlParser;
        this.textProtoParser = textProtoParser;
        this.codeLocationGenerator = codeLocationGenerator;
        this.bazelExternalIdExtractionFullRuleJsonProcessor = bazelExternalIdExtractionFullRuleJsonProcessor;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(final File bazelExe, final File workspaceDir, String bazelTarget, String fullRulesPath) {
        logger.debug("Bazel extractAndPublishResults()");
        try {
            codeLocationGenerator.setWorkspaceDir(workspaceDir);
            List<BazelExternalIdExtractionFullRule> fullRules;
            if (StringUtils.isNotBlank(fullRulesPath)) {
                fullRules = loadFullRulesFromFile(fullRulesPath);
                logger.debug(String.format("Read %d rule(s) from %s", fullRules.size(), fullRulesPath));
            } else {
                BazelExternalIdExtractionSimpleRules simpleRules = new BazelExternalIdExtractionSimpleRules(fullRulesPath);
                fullRules = simpleRules.getRules().stream()
                                      .map(RuleConverter::simpleToFull).collect(Collectors.toList());
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Using default rules:\n%s", bazelExternalIdExtractionFullRuleJsonProcessor.toJson(fullRules)));
                }
            }
            final ArtifactStringsExtractor artifactStringsExtractorXml = new ArtifactStringsExtractorXml(bazelDetailsQueryExecutor, bazelExe, xmlParser, workspaceDir, bazelTarget);
            final ArtifactStringsExtractor artifactStringsExtractorTextProto = new ArtifactStringsExtractorTextProto(bazelDetailsQueryExecutor, bazelExe, textProtoParser, workspaceDir, bazelTarget);
            final BazelExternalIdGenerator externalIdGenerator = new BazelExternalIdGenerator(executableRunner, bazelExe.getAbsolutePath(),
                artifactStringsExtractorXml,
                artifactStringsExtractorTextProto,
                workspaceDir, bazelTarget);
            fullRules.stream()
                .map(externalIdGenerator::generate)
                .flatMap(Collection::stream)
                .forEach(codeLocationGenerator::addDependency);
            if (externalIdGenerator.isErrors()) {
                return new Extraction.Builder().failure(externalIdGenerator.getErrorMessage()).build();
            }
            final List<CodeLocation> codeLocations = codeLocationGenerator.build();
            final String projectName = cleanProjectName(bazelTarget);
            final Extraction.Builder builder = new Extraction.Builder()
                                                   .success(codeLocations)
                                                   .projectName(projectName);
            return builder.build();
        } catch (Exception e) {
            final String msg = String.format("Bazel processing exception: %s", e.getMessage());
            logger.debug(msg, e);
            return new Extraction.Builder().failure(msg).build();
        }
    }

    private String cleanProjectName(final String bazelTarget) {
        String projectName = bazelTarget
                                 .replaceAll("^//", "")
                   .replaceAll("^:", "")
                   .replaceAll("/", "_")
                   .replaceAll(":", "_");
        return projectName;
    }

    private List<BazelExternalIdExtractionFullRule> loadFullRulesFromFile(final String fullRulesJsonFilePath) throws IOException {
        final File jsonFile = new File(fullRulesJsonFilePath);
        List<BazelExternalIdExtractionFullRule> loadedRules = bazelExternalIdExtractionFullRuleJsonProcessor.load(jsonFile);
        return loadedRules;
    }
}
