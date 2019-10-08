package com.example.sergushin.world;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sergushin on 1/13/2018.
 */

public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {
    private SvgFileResolver svgFileResolver;

    public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
        svgFileResolver = new SvgFileResolver();
        try {
            SVG svg = SVG.getFromInputStream(source);
            svg.registerExternalFileResolver(svgFileResolver);

            return new SimpleResource<SVG>(svg);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }

    @Override
    public String getId() {
        return "SvgDecoder.com.bumptech.svgsample.app";
    }
}