package org.jtwig.render.model;

import com.google.common.base.Optional;
import org.jtwig.model.expression.Expression;
import org.jtwig.model.position.Position;
import org.jtwig.model.tree.Node;

public class RenderNode extends Node {
    private final Expression pathExpression;
    private final Optional<Expression> withExpression;

    public RenderNode(Position position, Expression pathExpression, Optional<Expression> withExpression) {
        super(position);
        this.pathExpression = pathExpression;
        this.withExpression = withExpression;
    }

    public Expression getPathExpression() {
        return pathExpression;
    }

    public Optional<Expression> getWithExpression() {
        return withExpression;
    }
}
