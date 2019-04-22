package com.testnetdeve.unittest;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class JmeterNano extends AbstractJavaSamplerClient {
    @Override
    public Arguments getDefaultParameters() {
        return super.getDefaultParameters();
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        return null;
    }
}
