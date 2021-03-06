/*
 * Copyright 2014 ArcBees Inc.
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

package com.gwtplatform.dispatch.rest.client.interceptor;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingAsyncCallback;
import com.gwtplatform.dispatch.client.DelegatingDispatchRequest;
import com.gwtplatform.dispatch.client.DispatchCall;
import com.gwtplatform.dispatch.rest.client.core.DispatchCallFactory;
import com.gwtplatform.dispatch.rest.client.core.RestDispatchCall;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.TypedAction;

/**
 * {@code AsyncCallback} implementation wrapping another {@link AsyncCallback} object used by a {@link
 * com.gwtplatform.dispatch.client.interceptor.Interceptor Interceptor} implementations to delegate the execution
 * result.
 *
 * @param <A> the {@link TypedAction} type.
 * @param <R> the result type for this action.
 */
public class RestInterceptedAsyncCallback<A extends RestAction<R>, R>
        extends DelegatingAsyncCallback<A, R, RestInterceptor> {
    private final DispatchCallFactory dispatchCallFactory;

    public RestInterceptedAsyncCallback(
            DispatchCallFactory dispatchCallFactory,
            DispatchCall<A, R> dispatchCall,
            A action,
            AsyncCallback<R> callback,
            DelegatingDispatchRequest dispatchRequest) {
        super(dispatchCall, action, callback, dispatchRequest);

        this.dispatchCallFactory = dispatchCallFactory;
    }

    @Override
    public DispatchRequest execute(A action, AsyncCallback<R> resultCallback) {
        if (getDispatchRequest().isPending()) {
            RestDispatchCall<A, R> newDispatchCall = dispatchCallFactory.create(action, resultCallback);
            newDispatchCall.setIntercepted(true);

            return newDispatchCall.execute();
        } else {
            return null;
        }
    }
}
