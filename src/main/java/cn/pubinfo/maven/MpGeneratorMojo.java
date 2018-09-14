package cn.pubinfo.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * @author kuancz
 * @date 2018/9/14
 */
@Mojo(name = "generate")
public class MpGeneratorMojo extends AbstractMojo {
    @Parameter
    private List<String> tables;
    @Parameter
    private String auther;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Generator generator = new Generator(tables, auther);
        generator.start();
    }
}
