package com.jordanbunke.painterly.resources;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.ResourceLoader;

import java.nio.file.Path;

public final class ResourceReader {
    public static String read(final Path resource) {
        return FileIO.readResource(ResourceLoader.loadResource(resource), "");
    }
}
