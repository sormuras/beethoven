import com.github.sormuras.bach.ProjectInfo;
import com.github.sormuras.bach.ProjectInfo.Externals;
import com.github.sormuras.bach.ProjectInfo.Tools;
import com.github.sormuras.bach.ProjectInfo.Tweak;
import com.github.sormuras.bach.project.JavaStyle;

@ProjectInfo(
    name = "beethoven",
    version = "1-ea",
    format = JavaStyle.FREE,
    compileModulesForJavaRelease = 8,
    includeSourceFilesIntoModules = true,
    tools = @Tools(skip = {"jdeps", "javadoc", "jlink"}),
    lookupExternals = @Externals(name = Externals.Name.JUNIT, version = "5.8.0-M1"),
    testTweaks = @Tweak(tool = "jar(test.integration)", option = "-C", value = {"test.integration/test/resources", "."})
)
module bach.info {
  requires com.github.sormuras.bach;
}
