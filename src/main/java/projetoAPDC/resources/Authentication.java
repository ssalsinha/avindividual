package projetoAPDC.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import projetoAPDC.util.AuthToken;
import projetoAPDC.util.UserData;

@Path("/")
@Produces({ "application/json;charset=utf-8" })
public class Authentication {

	private final Datastore datastore = (Datastore) DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

	private static final Logger LOG = Logger.getLogger(Authentication.class.getName());

	public Authentication() {
	}

	@POST
	@Path("/register")
	@Consumes({ "application/json" })
	public Response register(UserData data) {

		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();

		} else {
			Transaction txn = datastore.newTransaction();

			try {
				Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.name);
				Entity user = this.datastore.get(userKey);

				if (user == null) {
					AuthToken token = new AuthToken(data.name);
					Key tokenKey = datastore.newKeyFactory()
							.addAncestor(PathElement.of("User", data.name))
							.setKind("Token")
							.newKey(token.tokenID);

					user = Entity.newBuilder(userKey)
							.set("password", DigestUtils.sha512Hex(data.password))
							.set("name", data.name)
							.set("address", data.address)
							.set("phone", data.phoneNR)
							.build();

					Entity tokenEnt = Entity.newBuilder(tokenKey)
							.set("id", token.tokenID)
							.set("creationData", token.creationDate)
							.set("expirationData", token.expirationDate)
							.build();

					txn.put(user, tokenEnt);
					txn.commit();
					return Response.ok(this.g.toJson(token)).build();
				}
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
				}
			}
		}
	}

	@POST
	@Path("/login")
	@Consumes({ "application/json" })
	public Response login(UserData data) {

		Transaction txn = datastore.newTransaction();

		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.name);

			Entity user = this.datastore.get(userKey);

			if (user != null) {
				AuthToken token = new AuthToken(data.name);
				Key tokenKey = datastore.newKeyFactory()
						.addAncestor(PathElement.of("User", data.name))
						.setKind("Token")
						.newKey(token.tokenID);

				String hashedPWD = user.getString("password");
				if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {

					Entity tokenEnt = Entity.newBuilder(tokenKey)
							.set("id", token.tokenID)
							.set("creationData", token.creationDate)
							.set("expirationData", token.expirationDate).build();

					txn.put(tokenEnt);
					txn.commit();

					return Response.ok(this.g.toJson(token)).build();
				}
				return Response.status(Status.FORBIDDEN).entity("Wrong password.").build();
			}
			return Response.status(Status.FORBIDDEN).entity("Wrong username").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/logout")
	@Consumes({ "application/json" })
	public Response Logout(AuthToken token) {

		Transaction txn = datastore.newTransaction();

		try {
			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.name))
					.setKind("Token")
					.newKey(token.tokenID);
			Entity tokenEnt = this.datastore.get(tokenKey);

			if (!this.validToken(token, tokenEnt)) {
				return Response.status(Status.FORBIDDEN).build();
			}

			txn.delete(tokenKey);
			txn.commit();
			return Response.ok().build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	

	@POST
	@Path("/delete")
	@Consumes({ "application/json" })
	public Response delete(AuthToken token) {

		Transaction txn = datastore.newTransaction();

		try {
			Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.name))
					.setKind("Token")
					.newKey(token.tokenID);
			Entity tokenEnt = this.datastore.get(tokenKey);

			if (!this.validToken(token, tokenEnt)) {
				return Response.status(Status.FORBIDDEN).build();
			}
			
	    	String name = token.name;

			
			Key userKey = datastore.newKeyFactory()
	        		 .setKind("User")
	        		 .newKey(name);

			txn.delete(tokenKey, userKey);
			txn.commit();
			return Response.ok().build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	

	/*
	@POST
	@Path("/update")
	@Consumes({ "application/json" })
	public Response update (AuthToken token, UserData data) {
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.name))
				.setKind("Token")
				.newKey(token.tokenID);
		Entity tokenEnt = this.datastore.get(tokenKey);
		
		if (!this.validToken(token, tokenEnt)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Transaction txn = datastore.newTransaction();
		
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.name);
			Entity user = this.datastore.get(userKey);
			
			String address = "";
			String phone = "";
			if (data.address.equals(""))
				address = user.getString("name");
			if (data.phoneNR.equals(""))
				phone = user.getString("name");
				
				
			
			user = Entity.newBuilder(userKey)
					.set("password", user.getString("password"))
					.set("name", user.getString("name"))
					.set("address", address)
					.set("phone", phone)
					.build();


			txn.put(user);
			txn.commit();
			return Response.ok(this.g.toJson(token)).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		
	}
	*/
	
	@POST
	@Path("/user")
	@Consumes({ "application/json" })
	public Response user (AuthToken token) {
		Key tokenKey = datastore.newKeyFactory().addAncestor(PathElement.of("User", token.name))
				.setKind("Token")
				.newKey(token.tokenID);
		Entity tokenEnt = this.datastore.get(tokenKey);
		
	    if (this.validToken(token, tokenEnt)) {
	    	String name = token.name;
	    	return Response.ok(this.g.toJson(name)).build();
	    	
	    }
	    else {
	    	 return Response.status(Status.NOT_FOUND).build();
	    }
	}

	

	
	private boolean validToken(AuthToken token, Entity tokenEnt) {
		return tokenEnt != null && token != null && token.tokenID.equals(tokenEnt.getString("id"))
				&& tokenEnt.getLong("expirationData") > System.currentTimeMillis();
	}

}
