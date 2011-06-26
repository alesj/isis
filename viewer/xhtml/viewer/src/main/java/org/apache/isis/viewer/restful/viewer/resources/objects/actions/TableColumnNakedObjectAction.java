/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.restful.viewer.resources.objects.actions;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.viewer.restful.viewer.resources.objects.TableColumnNakedObjectMember;
import org.apache.isis.viewer.restful.viewer.xom.ResourceContext;

public abstract class TableColumnNakedObjectAction extends TableColumnNakedObjectMember<ObjectAction> {

    TableColumnNakedObjectAction(final String headerText, final AuthenticationSession session,
        final ObjectAdapter nakedObject, final ResourceContext resourceContext) {
        super(headerText, session, nakedObject, resourceContext);
    }

    protected final String getOidStrRealTarget(final ObjectAction action) {
        final ObjectAdapter realTarget = action.realTarget(getNakedObject());
        return getOidStr(realTarget);
    }

}