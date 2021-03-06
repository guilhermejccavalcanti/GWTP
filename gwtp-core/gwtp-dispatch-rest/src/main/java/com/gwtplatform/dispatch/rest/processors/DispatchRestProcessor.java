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

package com.gwtplatform.dispatch.rest.processors;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.Path;

import com.google.auto.service.AutoService;
import com.gwtplatform.dispatch.rest.processors.resource.ResourcePostProcessors;
import com.gwtplatform.dispatch.rest.processors.resource.RootResource;
import com.gwtplatform.dispatch.rest.processors.resource.RootResourceFactory;
import com.gwtplatform.dispatch.rest.processors.resource.RootResourceProcessor;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessors;
import com.gwtplatform.processors.tools.GwtSourceFilter;
import com.gwtplatform.processors.tools.bindings.BindingsProcessors;
import com.gwtplatform.processors.tools.exceptions.UnableToProcessException;
import com.gwtplatform.processors.tools.logger.Logger;
import com.gwtplatform.processors.tools.outputter.Outputter;
import com.gwtplatform.processors.tools.utils.Utils;

import static com.google.auto.common.MoreElements.isType;
import static com.gwtplatform.dispatch.rest.processors.DispatchRestProcessor.GWT_MODULE_OPTION;
import static com.gwtplatform.processors.tools.logger.Logger.DEBUG_OPTION;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({DEBUG_OPTION, GWT_MODULE_OPTION})
public class DispatchRestProcessor extends AbstractProcessor {
    static final String GWT_MODULE_OPTION = "gwtp.module";

    private static final String UNABLE_TO_PROCESS_GENERAL = "Unable to process Rest-Dispatch classes.";
    private static final String UNABLE_TO_PROCESS_RESOURCE = "Unable to process resource.";
    private static final String UNRESOLVABLE_EXCEPTION = "Unresolvable exception.";

    private Logger logger;
    private Utils utils;
    private GwtSourceFilter sourceFilter;
    private RootResource.Factory rootResourceFactory;
    private RootResourceProcessor resourceProcessor;
    private ResourcePostProcessors resourcePostProcessors;
    private SerializationProcessors serializationProcessors;
    private BindingsProcessors bindingsProcessors;
    private Outputter outputter;

    public DispatchRestProcessor() {
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        initializeTools(processingEnv);
        initializeDomainFactory();
        initializeProcessors();
    }

    private void initializeTools(ProcessingEnvironment processingEnv) {
        logger = new Logger(processingEnv.getMessager(), processingEnv.getOptions());
        utils = new Utils(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
        outputter = new Outputter(logger, this, processingEnv.getFiler());
        sourceFilter = createSourceFilter();
    }

    private GwtSourceFilter createSourceFilter() {
        GwtSourceFilter filter = new GwtSourceFilter(logger, utils);
        String module = processingEnv.getOptions().get(GWT_MODULE_OPTION);

        if (module != null) {
            filter.addModules(module.split(","));
        }

        return filter;
    }

    private void initializeDomainFactory() {
        rootResourceFactory = new RootResourceFactory(logger, utils);
    }

    private void initializeProcessors() {
        resourceProcessor = new RootResourceProcessor(logger, utils, outputter);
        resourcePostProcessors = new ResourcePostProcessors(logger, utils, outputter);
        serializationProcessors = new SerializationProcessors(logger, utils, outputter);
        bindingsProcessors = new BindingsProcessors(logger, utils, outputter);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Path.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Path.class);
            elements = sourceFilter.filterElements(elements);

            processVisibleElements(elements, roundEnv);
        } catch (UnableToProcessException e) {
            logger.error(UNABLE_TO_PROCESS_GENERAL);
        } catch (Exception e) {
            logger.error().throwable(e).log(UNRESOLVABLE_EXCEPTION);
        }

        return false;
    }

    private void processVisibleElements(Set<? extends Element> elements, RoundEnvironment roundEnv) {
        for (Element element : elements) {
            if (isType(element)) {
                process(element);
            }
        }

        if (roundEnv.processingOver()) {
            processLastRound();
        }
    }

    private void process(Element element) {
        try {
            RootResource resource = rootResourceFactory.create(element);

            resourceProcessor.process(resource);
            resourcePostProcessors.process(resource);
        } catch (UnableToProcessException e) {
            logger.mandatoryWarning().context(element).log(UNABLE_TO_PROCESS_RESOURCE);
        }
    }

    private void processLastRound() {
        resourceProcessor.processLast();
        serializationProcessors.processLast();
        bindingsProcessors.processLast();
    }
}
