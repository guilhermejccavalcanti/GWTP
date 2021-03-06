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

package com.gwtplatform.processors.tools.bindings;

import com.google.common.base.Optional;
import com.gwtplatform.processors.tools.domain.Type;

public class BindingContext {
    private final Type moduleType;
    private final Type implementer;
    private final boolean subModule;

    private Optional<Type> implemented;
    private Optional<Type> scope;
    private boolean eagerSingleton;

    public BindingContext(
            Type moduleType,
            Type implementer) {
        this(moduleType, implementer, false);
    }

    public BindingContext(
            Type moduleType,
            Type implementer,
            boolean subModule) {
        this.moduleType = moduleType;
        this.implementer = implementer;
        this.subModule = subModule;
        this.implemented = Optional.absent();
        this.scope = Optional.absent();
    }

    public BindingContext(
            Type moduleType,
            Type implementer,
            Type implemented,
            Class<?> scope) {
        this.moduleType = moduleType;
        this.implementer = implementer;
        this.implemented = Optional.of(implemented);
        this.scope = Optional.of(new Type(scope));
        this.subModule = false;
    }

    public Type getModuleType() {
        return moduleType;
    }

    public Type getImplementer() {
        return implementer;
    }

    public Optional<Type> getImplemented() {
        return implemented;
    }

    public void setImplemented(Type implemented) {
        this.implemented = Optional.fromNullable(implemented);
    }

    public Optional<Type> getScope() {
        return scope;
    }

    public void setScope(Class<?> scope) {
        this.scope = Optional.of(new Type(scope));
    }

    public boolean isEagerSingleton() {
        return eagerSingleton;
    }

    public void setEagerSingleton(boolean eagerSingleton) {
        this.eagerSingleton = eagerSingleton;
    }

    public boolean isSubModule() {
        return subModule;
    }
}
