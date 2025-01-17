/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;

import freemarker.template.Configuration;

//Everything a DetectRun needs supplied from Boot. Ideally the minimum subset of things needed to be passed from Boot to Run.
public class BootSingletons {
    private final ProductRunData productRunData;

    private final DetectRun detectRun;
    private final Gson gson;
    private final DetectInfo detectInfo;

    private final FileFinder fileFinder;
    private final EventSystem eventSystem;
    private final DetectorProfiler detectorProfiler;

    private final PropertyConfiguration detectConfiguration;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final DetectableOptionFactory detectableOptionFactory; //This is the only one that I do not believe should be here. Does not feel like it should be owned by boot. Currently requires 'diagnostics' sadly. - jp

    private final DirectoryManager directoryManager;
    private final Configuration configuration;

    public BootSingletons(final ProductRunData productRunData, final DetectRun detectRun, final Gson gson, final DetectInfo detectInfo, final FileFinder fileFinder, final EventSystem eventSystem,
        final DetectorProfiler detectorProfiler,
        final PropertyConfiguration detectConfiguration, final DetectableOptionFactory detectableOptionFactory, final DetectConfigurationFactory detectConfigurationFactory,
        final DirectoryManager directoryManager,
        final Configuration configuration) {
        this.productRunData = productRunData;
        this.detectRun = detectRun;
        this.gson = gson;
        this.detectInfo = detectInfo;
        this.fileFinder = fileFinder;
        this.eventSystem = eventSystem;
        this.detectorProfiler = detectorProfiler;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationFactory = detectConfigurationFactory;
        this.detectableOptionFactory = detectableOptionFactory;
        this.directoryManager = directoryManager;
        this.configuration = configuration;
    }

    public ProductRunData getProductRunData() {
        return productRunData;
    }

    public PropertyConfiguration getDetectConfiguration() {
        return detectConfiguration;
    }

    public DetectConfigurationFactory getDetectConfigurationFactory() {
        return detectConfigurationFactory;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public DetectInfo getDetectInfo() {
        return detectInfo;
    }

    public Gson getGson() {
        return gson;
    }

    public FileFinder getFileFinder() {
        return fileFinder;
    }

    public DetectRun getDetectRun() {
        return detectRun;
    }

    public DetectorProfiler getDetectorProfiler() {
        return detectorProfiler;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public DetectableOptionFactory getDetectableOptionFactory() {
        return detectableOptionFactory;
    }
}
