package com.daniel.pods.model;

import java.net.URI;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inrupt.client.Headers;
import com.inrupt.client.solid.SolidRDFSource;
import com.inrupt.rdf.wrapping.commons.TermMappings;
import com.inrupt.rdf.wrapping.commons.ValueMappings;
import com.inrupt.rdf.wrapping.commons.WrapperIRI;
@JsonIgnoreProperties(value = { "metadata", "headers", "graph", "graphNames", "entity", "contentType" })
public class ClientData extends SolidRDFSource{
    static IRI SCHEMA_ORG_ADRESS= rdf.createIRI("https://schema.org/address");
    private final Node subject;
    @JsonIgnoreProperties(value = { "metadata", "headers", "graph", "graphNames", "entity", "contentType" })
    public ClientData(final URI identifier, final Dataset dataset, final Headers headers) {
        super(identifier, dataset, headers);
        this.subject = new Node(rdf.createIRI(identifier.toString()), getGraph());
    }   
    public ClientData(final URI identifier) {
        this(identifier, null, null);
    }
    @JsonCreator
    public ClientData(
        @JsonProperty("identifier") final URI identifier,
        @JsonProperty("address") String address) {
            this(identifier);
            this.setAddress(address);
    }


    public void setAddress(final String address){
        subject.setAddress(address);
    }
    public String getAddress(){
        return subject.getAddress();
    }

    class Node extends WrapperIRI{
        Node(final RDFTerm original, final Graph graph){
            super(original,graph);
        }
        String getAddress(){
            return anyOrNull(SCHEMA_ORG_ADRESS, ValueMappings::literalAsString);
        }
        void setAddress(final String value){
            overwriteNullable(SCHEMA_ORG_ADRESS, value  , TermMappings::asStringLiteral);
        }
    }
}
