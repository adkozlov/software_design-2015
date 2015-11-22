package ru.spbau.kozlov.shell.api.commands.jcommander;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.kozlov.shell.CommandLoader;
import ru.spbau.kozlov.shell.api.commands.AbstractGrepCommand;
import ru.spbau.kozlov.shell.api.commands.Command;
import ru.spbau.kozlov.shell.api.invoker.ExecutionException;
import ru.spbau.kozlov.shell.api.invoker.PrintUsageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author adkozlov
 */
public class GrepCommandTest {

    private static Class<? extends Command> grepCommandClass;

    @BeforeClass
    public static void setUpBeforeClass() throws ClassNotFoundException {
        grepCommandClass = CommandLoader.loadCommands(".", "ru.spbau.kozlov.shell.api.commands.jcommander.GrepCommand")
                .findFirst().get();
    }

    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    private AbstractGrepCommand command;

    @Before
    public void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        command = (AbstractGrepCommand) grepCommandClass.newInstance();
    }

    @Test
    public void testGrep() throws Exception {
        command.execute(createInputStream("cat", "cat | wc", "wc", "cat"),
                printStream,
                Collections.singletonList("wc"));
        assertEquals(createStringWithLineSeparators("cat | wc", "wc"), outputStream.toString());
    }

    @Test
    public void testCaseInsensitiveGrep() throws Exception {
        command.execute(createInputStream("cat", "cat | Wc", "WC", "cat"),
                printStream,
                Arrays.asList("-i", "Wc"));
        assertEquals(createStringWithLineSeparators("cat | Wc", "WC"), outputStream.toString());
    }

    @Test
    public void testRegexpGrep() throws Exception {
        command.execute(createInputStream("cat", "cat | wc", "wc", "cat wc"),
                printStream,
                Collections.singletonList("^wc"));
        assertEquals(createStringWithLineSeparators("wc"), outputStream.toString());
    }

    @Test
    public void testWordRegexpGrep() throws Exception {
        command.execute(createInputStream("cat", "cat | wc", "cat"),
                printStream,
                Arrays.asList("-w", "at"));
        assertArrayEquals(new byte[]{}, outputStream.toByteArray());
    }

    @Test
    public void testWordsAfterGrep() throws Exception {
        command.execute(createInputStream("cat", "cat | wc", "cat", "wc", "cat", "cat"),
                printStream,
                Arrays.asList("-A", "1", "wc"));
        assertEquals(createStringWithLineSeparators("cat | wc", "cat", "wc", "cat"), outputStream.toString());
    }

    @Test(expected = ExecutionException.class)
    public void testWordsAfterGrepIllegalArgument() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Arrays.asList("-A", "text", "wc"));
    }

    @Test(expected = PrintUsageException.class)
    public void testPrintUsage() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Collections.emptyList());
    }

    @Test(expected = PrintUsageException.class)
    public void testPrintUsageOnEmptyInput() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Collections.singletonList("wc"));
    }

    @Test
    public void testEmptyFile() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Arrays.asList("wc", "empty_file"));
        assertEquals(createStringWithLineSeparators(), outputStream.toString());
    }

    @Test
    public void testNonEmptyFile() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Arrays.asList("cp", "compile.sh"));
        assertEquals(createStringWithLineSeparators("cp -r ../../src/ru ."), outputStream.toString());
    }

    @Test
    public void testFileNotFound() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Arrays.asList("wc", "file"));
        assertEquals(createStringWithLineSeparators("grep: file (No such file or directory)"), outputStream.toString());
    }

    @Test
    public void testFileIsDirectory() throws Exception {
        command.execute(createInputStream(),
                printStream,
                Arrays.asList("wc", "."));
        assertEquals(createStringWithLineSeparators("grep: . (Is a directory)"), outputStream.toString());
    }

    private static ByteArrayInputStream createInputStream(String... strings) {
        return new ByteArrayInputStream(createStringWithLineSeparators(strings).getBytes());
    }

    private static String createStringWithLineSeparators(String... strings) {
        return strings.length != 0 ?
                String.join(System.lineSeparator(), strings) + System.lineSeparator() :
                "";
    }
}