package org.jtwig.render;

import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.extension.Extension;
import org.jtwig.render.addon.RenderNodeAddonProvider;

public class RenderExtension implements Extension {
    @Override
    public void configure(EnvironmentConfigurationBuilder configurationBuilder) {
        configurationBuilder.parser().withAddOnParser(new RenderNodeAddonProvider());
    }
}
