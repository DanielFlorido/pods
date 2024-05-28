package com.daniel.pods.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import org.springframework.web.bind.annotation.RestController;
import com.inrupt.client.solid.PreconditionFailedException;
import com.inrupt.client.solid.ForbiddenException;
import com.inrupt.client.solid.NotFoundException;
import com.daniel.pods.model.ClientData;
import com.daniel.pods.model.Expense;
import com.inrupt.client.auth.Session;
import com.inrupt.client.openid.OpenIdSession;
import com.inrupt.client.solid.SolidSyncClient;

import org.apache.commons.rdf.api.RDFSyntax;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
public class ClientDataController {

    final Session session = OpenIdSession.ofClientCredentials(
            URI.create("https://login.inrupt.com"),
            "f221d46a-f7a2-4c82-8adb-e9d071fd8c1f",
            "4b1257db-b4cb-4cdc-9f48-9cb59ee4dd3c",
            "client_secret_basic");

    final SolidSyncClient client = SolidSyncClient.getClient().session(session);
    private final PrintWriter printWriter = new PrintWriter(System.out, true);

    @PostMapping("/clientData/create")
    public ClientData createClientData(@RequestBody ClientData newClientData) {
        printWriter.println("ClientDataController:: createClient");
        try(var createdClientData = client.create(newClientData)){
            printClientDataAsTurtle(newClientData);
            return createdClientData;
        } catch(PreconditionFailedException e1) {
            // Errors if the resource already exists
            printWriter.println(String.format("[%s] com.inrupt.client.solid.PreconditionFailedException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to create
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;    
    }
    @GetMapping("/clientData/get")
    public ClientData getClientData(@RequestParam(value="resourceURL", defaultValue = "") String resourceURL) {
        try(var resource = client.read(URI.create(resourceURL), ClientData.class)){
            return resource;
        } catch(PreconditionFailedException e1) {
            // Errors if the resource already exists
            printWriter.println(String.format("[%s] com.inrupt.client.solid.PreconditionFailedException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to create
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null; 
    }
    @PutMapping("/clientData/update")
    public ClientData updateClientData(@RequestBody ClientData clientData) {
        printWriter.println("ClientData Controller:: updateClientData");

        try(var updateClientData= client.update(clientData)) {
            printClientDataAsTurtle(updateClientData);
            return updateClientData;
        } catch (NotFoundException e1) {
            // Errors if resource is not found
            printWriter.println(String.format("[%s] com.inrupt.client.solid.NotFoundException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to read
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @DeleteMapping("/clientData/delete")
    public void deleteClienteData(@RequestParam(value = "resourceURL") String resource){
        printWriter.println("ExpenseController:: deleteExpense");
        try {
            client.delete(URI.create(resource));

            // Alternatively, you can specify an Expense object to the delete method.
            // The delete method deletes  the Expense recorde located in the Expense.identifier field. 
            // For example: client.delete(new Expense(URI.create(resourceURL)));
        } catch (NotFoundException e1) {
            // Errors if resource is not found
            printWriter.println(String.format("[%s] com.inrupt.client.solid.NotFoundException:: %s", e1.getStatusCode(), e1.getMessage()));
        } catch(ForbiddenException e2) {
            // Errors if user does not have access to read
            printWriter.println(String.format("[%s] com.inrupt.client.solid.ForbiddenException:: %s", e2.getStatusCode(), e2.getMessage()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void printClientDataAsTurtle(ClientData clientData) {
        printWriter.println("ExpenseController:: printExpenseAsTurtle");
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        try  {
            clientData.serialize(RDFSyntax.TURTLE, content);
            printWriter.println(content.toString("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
