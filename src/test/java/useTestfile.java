import java.io.File;

public class useTestfile {
	public static void main(String[] args) {
		String relativePathOfHardCopy = "src/main/resources/Cryptocurrency Screener - Yahoo Finance.html";
		String absolutePath = new File(relativePathOfHardCopy).getAbsoluteFile().toString();
		String url = "file:///" + absolutePath;

		Main.performScraping(url);
	}
}
