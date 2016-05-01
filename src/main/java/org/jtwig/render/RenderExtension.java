package org.jtwig.render;

import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.extension.Extension;
import org.jtwig.render.addon.RenderNodeAddonProvider;
import org.jtwig.render.model.RenderNode;
import org.jtwig.render.node.RenderNodeRender;

public class RenderExtension implements Extension {
    @Override
    public void configure(EnvironmentConfigurationBuilder configurationBuilder) {
        configurationBuilder.parser().addonParserProviders().add(new RenderNodeAddonProvider()).and()
            .and().render().nodeRenders().add(RenderNode.class, new RenderNodeRender());
    }
}
