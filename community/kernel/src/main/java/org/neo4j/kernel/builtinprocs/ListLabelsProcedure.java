/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.builtinprocs;

import org.neo4j.collection.RawIterator;
import org.neo4j.graphdb.Label;
import org.neo4j.kernel.api.exceptions.ProcedureException;
import org.neo4j.kernel.api.proc.Neo4jTypes;
import org.neo4j.kernel.api.proc.CallableProcedure;
import org.neo4j.kernel.api.proc.ProcedureSignature.ProcedureName;
import org.neo4j.kernel.impl.api.TokenAccess;

import static org.neo4j.helpers.collection.Iterables.asRawIterator;
import static org.neo4j.helpers.collection.Iterables.map;
import static org.neo4j.kernel.api.proc.ProcedureSignature.procedureSignature;

public class ListLabelsProcedure extends CallableProcedure.BasicProcedure
{
    public ListLabelsProcedure( ProcedureName name )
    {
        super( procedureSignature( name ).out( "label", Neo4jTypes.NTString ).build() );
    }

    @Override
    public RawIterator<Object[], ProcedureException> apply( Context ctx, Object[] input ) throws ProcedureException
    {
        RawIterator<Label,ProcedureException> labels = asRawIterator( TokenAccess.LABELS.inUse( ctx.get( Context.KERNEL_TRANSACTION ).acquireStatement() ) );
        return map( ( l) -> new Object[]{l.name()}, labels );
    }
}