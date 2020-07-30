$(function(){
	var name = "Getting username...";
	document.getElementById('output').innerHTML = name;

       $.ajax({
        contentType: 'application/json;charset=UTF-8',
        type: "POST",
        url: "https://aindi-276521.appspot.com/rest/user",
        data: Cookies.get("token"),
        success: function(user){
        	var name2 = user;
        	document.getElementById('output').innerHTML = name2;
        },
        error: function(){
        	var name2 = "Not logged in.";
        	document.getElementById('output').innerHTML = name2;
        }
    });
    
       $("#logoutBtn").on("click", function(){
           $.ajax({
   			contentType: 'application/json;charset=UTF-8',
   			type: "POST",
   			url: "https://aindi-276521.appspot.com/rest/logout",
   			data: Cookies.get("token"),
   			success: function(){
                Cookies.remove("token");
                location.href = "home.html";
   			},
   			error: function(){
   				alert("Not logged in.");
   			}
   		});
       });
       
       $("#deleteBtn").on("click", function(){
           $.ajax({
   			contentType: 'application/json;charset=UTF-8',
   			type: "POST",
   			url: "https://aindi-276521.appspot.com/rest/delete",
   			data: Cookies.get("token"),
   			success: function(){
                Cookies.remove("token");
                alert("User deleted.");
                location.href = "home.html";
   			},
   			error: function(){
   				alert("Not logged in.");
   			}
   		});
       });
});

function register(){
    location.href = "register.html";
}

function login(){
    location.href = "login.html";
}





