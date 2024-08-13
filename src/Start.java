import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import net.minecraft.client.main.LaunchWrapper;
import net.minecraft.client.main.Main;

public class Start
{
    public static void main(String[] args) throws IOException {
        InputStream dllStream = Start.class.getResourceAsStream("assets/minecraft/xylitol/lwjgl64.dll");
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File dllFile = new File(tempDir, "lwjgl64.dll");
        Files.copy(dllStream, dllFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        String nativePath = tempDir.getAbsolutePath();

        System.setProperty("java.library.path",nativePath);
        System.setProperty("org.lwjgl.librarypath",nativePath);
        LaunchWrapper.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
