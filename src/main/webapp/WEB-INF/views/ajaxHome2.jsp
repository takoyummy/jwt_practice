<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title>Home</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<!-- 
<script src="/js/jQuery-2.1.4.min.js"></script>
 -->
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<script type="text/javascript">
    var ACCESS_TOKEN = "";
    $(document).ready(function () {
        $("#loginBtn").on("click", function () {
            // *** 이벤트 처리 로직 구현 *** //
            var userId = $("#userId");
            var password = $("#password");
            var userIdVal = userId.val();
            var passwordVal = password.val();

            var userObject = {
                username: userIdVal,
                password: passwordVal
            };

            $.ajax({
                type: "POST",
                url: "/login",
                data: JSON.stringify(userObject),
                contentType: "application/json; charset=UTF-8",
                success: function (data) {
                    console.log("data: " + data);
                    ACCESS_TOKEN = data;
                    alert(ACCESS_TOKEN);
                }
            });
        });
        $("#readBtn").on("click", function () {
            // *** 이벤트 처리 로직 구현 *** //
            $.ajax({   
                type : "GET",
                url : "/read",
                contentType : "application/json; charset=UTF-8",
                headers : {  "Authorization" : "Bearer " + ACCESS_TOKEN  },
                success : function(data) {  
                    console.log("data: " + data);     
                    alert(JSON.stringify(data));    
                    console.log("data.token: " + data.token);  
                },  
                error : function(xhr, status, error) {
                    alert("code:" + xhr.status + "\n"  + "message:" + xhr.responseText + "\n"  + "error:" + error);  
                }     
        });


        });
    });     

     

</script>
<body>
    <h1>Register Form</h1>
	<form>
		userId: <input type="text" name="userId" value="hongkd" id="userId"><br>
		password: <input type="text" name="password" value="3456"
			id="password"><br>
	</form>
	<button id="loginBtn">login</button>   </button>  <button id="readBtn">read</button>
</body>
</html>