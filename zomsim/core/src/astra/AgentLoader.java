package astra;

import astra.core.ASTRAClass;
import astra.core.Agent;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class AgentLoader {
    private File rootDir;
    private Map<File, URLClassLoader> loaders = new HashMap<>();

    public AgentLoader(String folder) {
        rootDir = new File(folder);
        System.out.println(rootDir.getAbsolutePath());
    }

    public void scan() throws MalformedURLException {
        for (File file : rootDir.listFiles()) {
            if (!loaders.containsKey(file) && file.getName().endsWith("jar")) {
                System.out.println("Adding: " + file.getName());
                loaders.put(file, new URLClassLoader(new URL[] {file.toURI().toURL()}, getClass().getClassLoader()));
            }
        }
    }

    public Agent createAgent(String clazz, String name) throws Exception {
        for (URLClassLoader loader : loaders.values()) {
            Class<?> agentClass = loader.loadClass(clazz);
            if (agentClass != null) {
                return ((ASTRAClass) agentClass.getConstructor().newInstance()).newInstance(name);
            }
        }
        return null;
    }

    public void close() throws IOException {
        for (URLClassLoader loader : loaders.values()) {
            loader.close();
        }
        loaders.clear();
    }
}
