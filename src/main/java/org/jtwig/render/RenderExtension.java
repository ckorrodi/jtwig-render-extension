package org.jtwig.render;

import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.extension.Extension;
import org.jtwig.render.addon.RenderNodeAddonProvider;
import org.jtwig.render.model.RenderNode;
import org.jtwig.render.node.RenderNodeRender;

public class RenderExtension implements Extension {
    @Override
    public void configure(EnvironmentConfigurationBuilder configurationBuilder) {
        configurationBuilder.parser().withAddonParserProvider(new RenderNodeAddonProvider())
            .and().render().withRender(RenderNode.class, new RenderNodeRender());
    }
}
