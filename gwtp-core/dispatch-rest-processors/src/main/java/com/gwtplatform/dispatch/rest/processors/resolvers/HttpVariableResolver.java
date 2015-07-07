/*
 * Copyright 2015 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rest.processors.resolvers;

import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.gwtplatform.dispatch.rest.processors.definitions.HttpAnnotationDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.HttpVariableDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.logger.Logger;

public class HttpVariableResolver {
    private final HttpAnnotationResolver httpAnnotationResolver;
    private final DateFormatResolver dateFormatResolver;

    public HttpVariableResolver(
            Logger logger,
            Types types,
            Elements elements) {
        this.httpAnnotationResolver = new HttpAnnotationResolver(logger, types, elements);
        this.dateFormatResolver = new DateFormatResolver(logger);
    }

    public boolean canResolve(VariableElement element) {
        return httpAnnotationResolver.canResolve(element)
                && dateFormatResolver.canResolve(element);
    }

    public HttpVariableDefinition resolve(VariableElement element) {
        TypeDefinition type = new TypeDefinition(element.asType());
        String name = element.getSimpleName().toString();
        HttpAnnotationDefinition httpAnnotation = httpAnnotationResolver.resolve(element);
        String dateFormat = dateFormatResolver.resolve(element);
        boolean body = httpAnnotation == null;

        return new HttpVariableDefinition(type, name, httpAnnotation, dateFormat, body);
    }
}