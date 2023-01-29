function login() {
    var user = document.getElementById('email').value;
    console.log(user);
    var pass = document.getElementById('pass').value;
    console.log(pass);
    var req = new XMLHttpRequest();

    req.open("POST", "http://localhost:8080/archery/auth/signIn", false);
    req.setRequestHeader('Accept', 'application/json');
    req.setRequestHeader('Content-Type', 'application/json');
    req.withCredentials = true;

    req.onload = function () {
        if (req.status !== 200) {
            alert("Wrong email or password, please try again");
        } else {

            console.log(req);
            console.log("Status is 200");
            setToken(req);
            console.log(JSON.parse(localStorage.getItem('tokenData')));
            console.log(JSON.parse(localStorage.getItem('expiresIn')));
            document.location.href = "../index2.html";

            // let jwt = JSON.parse(localStorage.getItem('tokenData'));
            // let jwtData = jwt.split('.')[1]
            // let decodedJwtJsonData = window.atob(jwtData)
            // let decodedJwtData = JSON.parse(decodedJwtJsonData)
            //
            // let isAdmin = decodedJwtData.roles
            //
            // console.log('jwtData: ' + jwtData)
            // console.log('decodedJwtJsonData: ' + decodedJwtJsonData)
            // console.log('decodedJwtData: ' + decodedJwtData)
            // console.log('Is admin: ' + isAdmin)
        }
    }
    req.send(JSON.stringify({login: user, password: pass}));
}

function setToken(tokenData) {
    console.log(tokenData.responseText);
    localStorage.setItem('tokenData', JSON.stringify(tokenData.responseText));
    localStorage.setItem('expiresIn', JSON.stringify(Date.now() + 180000)); //захардкодила, подумать как изменить (мб добавить новый метод)
}

