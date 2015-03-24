package lukazitnik.jshint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

public class JSHintTest {

    @Test
    public void testLint() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("index.js");
        PrintWriter out = (new PrintWriter(fo.getOutputStream()));
        out.write("a;");
        out.close();

        JSHint jshint = JSHint.instance;
        LinkedList<JSHintError> errors = jshint.lint(fo);
        JSHintError head = errors.element();

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(1 == head.getLine());
        Assert.assertEquals("Expected an assignment or function call and instead saw an expression.", head.getReason());
    }

    @Test
    public void testFindFile() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject hasFile = fs.getRoot().createFolder("hasFile");
        FileObject file = hasFile.createData("file");
        FileObject childFolder = hasFile.createFolder("childFolder");

        FileObject result = JSHint.findFile("file", childFolder);
        Assert.assertEquals(file, result);
    }

    @Test
    public void testLintWithConfig() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();

        FileObject jsFo = fs.getRoot().createData("index.js");
        PrintWriter jsOut = (new PrintWriter(jsFo.getOutputStream()));
        jsOut.write("while (day)\n  shuffle();");
        jsOut.close();

        FileObject configFo = fs.getRoot().createData(".jshintrc");
        PrintWriter configOut = (new PrintWriter(configFo.getOutputStream()));
        configOut.write("{\"curly\":true,\"undef\":true}");
        configOut.close();

        JSHint jshint = JSHint.instance;
        LinkedList<JSHintError> errors = jshint.lint(jsFo);

        Assert.assertEquals(3, errors.size());
        Assert.assertEquals("'shuffle' is not defined.", errors.pop().getReason());
        Assert.assertEquals("'day' is not defined.", errors.pop().getReason());
        Assert.assertEquals("Expected '{' and instead saw 'shuffle'.", errors.pop().getReason());
    }

}
