import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Manage resources across jarfiles (for both the application and velocity).
 * Required for Velocity's build system to properly package and deploy
 * applications.
 */
public abstract class ResourceLoader {
    /**
     * Main application resource loader.
     */
    private static ResourceLoader appLdr;

    /**
     * Register the app resource loader if one doesn't already exist.
     * 
     * @param ldr The loader to register.
     */
    public static void registerAppResourceLoader(ResourceLoader ldr) {
        appLdr = ldr;
    }

    /**
     * Get the assigned app resource loader.
     * 
     * @return The resource loader.
     */
    public static ResourceLoader getAppLoader() {
        return appLdr;
    }

    /**
     * Loads an input stream from a provided file path within a jar file.
     * 
     * @param filePath Path within the jar to load from.
     * @return The loaded input stream.
     * @throws IOException If there is no file or it cannot be loaded.
     */
    public abstract BufferedInputStream load(String filePath) throws IOException;
}
