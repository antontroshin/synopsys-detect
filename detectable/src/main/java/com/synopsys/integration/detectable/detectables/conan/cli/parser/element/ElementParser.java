package com.synopsys.integration.detectable.detectables.conan.cli.parser.element;

import java.util.List;

import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public interface ElementParser {
    boolean applies(String elementLine);

    int parseElement(List<String> conanInfoOutputLines, int bodyElementLineIndex, ConanNodeBuilder nodeBuilder);
}