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


package org.apache.isis.core.progmodel.facets.object.notpersistable;

import org.apache.isis.applib.marker.NonPersistable;
import org.apache.isis.applib.marker.ProgramPersistable;
import org.apache.isis.core.metamodel.facets.Facet;
import org.apache.isis.core.metamodel.spec.feature.ObjectFeatureType;
import org.apache.isis.core.progmodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.progmodel.facets.object.notpersistable.NotPersistableFacet;
import org.apache.isis.core.progmodel.facets.object.notpersistable.NotPersistableFacetMarkerInterface;
import org.apache.isis.core.progmodel.facets.object.notpersistable.NotPersistableMarkerInterfacesFacetFactory;


public class NotPersistableMarkerInterfaceFacetFactoryTest extends AbstractFacetFactoryTest {

    private NotPersistableMarkerInterfacesFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new NotPersistableMarkerInterfacesFacetFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    @Override
    public void testFeatureTypes() {
        final ObjectFeatureType[] featureTypes = facetFactory.getFeatureTypes();
        assertTrue(contains(featureTypes, ObjectFeatureType.OBJECT));
        assertFalse(contains(featureTypes, ObjectFeatureType.PROPERTY));
        assertFalse(contains(featureTypes, ObjectFeatureType.COLLECTION));
        assertFalse(contains(featureTypes, ObjectFeatureType.ACTION));
        assertFalse(contains(featureTypes, ObjectFeatureType.ACTION_PARAMETER));
    }

    public void testProgramPersistableMeansNotPersistableByUser() {
        class Customer implements ProgramPersistable {}

        facetFactory.process(Customer.class, methodRemover, facetHolder);

        final Facet facet = facetHolder.getFacet(NotPersistableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistableFacetMarkerInterface);
        final NotPersistableFacetMarkerInterface notPersistableFacetMarkerInterface = (NotPersistableFacetMarkerInterface) facet;
        final org.apache.isis.core.progmodel.facets.object.notpersistable.InitiatedBy value = notPersistableFacetMarkerInterface.value();
        assertEquals(org.apache.isis.core.progmodel.facets.object.notpersistable.InitiatedBy.USER, value);

        assertNoMethodsRemoved();
    }

    public void testNotPersistable() {
        class Customer implements NonPersistable {}

        facetFactory.process(Customer.class, methodRemover, facetHolder);

        final Facet facet = facetHolder.getFacet(NotPersistableFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof NotPersistableFacetMarkerInterface);
        final NotPersistableFacetMarkerInterface notPersistableFacetMarkerInterface = (NotPersistableFacetMarkerInterface) facet;
        final org.apache.isis.core.progmodel.facets.object.notpersistable.InitiatedBy value = notPersistableFacetMarkerInterface.value();
        assertEquals(org.apache.isis.core.progmodel.facets.object.notpersistable.InitiatedBy.USER_OR_PROGRAM, value);

        assertNoMethodsRemoved();
    }

}
