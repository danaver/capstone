// $(document).ready(function(){
    var config = {
    apiKey: "AIzaSyD2fk6WQNgH6WAekIJ3zbZCt1wRJ3kuP6U",
    authDomain: "bistalk-7833f.firebaseapp.com",
    databaseURL: "https://bistalk-7833f.firebaseio.com",
    projectId: "bistalk-7833f",
    storageBucket: "bistalk-7833f.appspot.com",
    messagingSenderId: "1004391529913"
  };

  firebase.initializeApp(config);
  var rootRef = firebase.database().ref().child('wordbank');
  var usersRef = firebase.database().ref().child('User');

  rootRef.on("child_added", snap =>{
    var english = snap.child("English").val();
    var cebuano = snap.child("Cebuano").val();
    var pronunciation = snap.child("Pronunciation").val();
    var pos = snap.child("POS").val();

    var picture = snap.child("Picture").val();
    var audio = snap.child("Audio").val();
    var effect = snap.child("Effect").val();    

    // $("#table_body").append("<tr><td>" + english +"</td><td>" +cebuano + "</td><td>" +pronunciation+
  //   //                         "</td><td>" + pos + "</td><td><button>Pic</button></td><tr>" );
  // $("#table_body").append("<tr><td>" + english +"</td><td>"+cebuano + "</td><td>" +pronunciation+
  //                           "</td><td>" + pos + "</td><td>" + picture + "</td><td>" + "audio" "</td><td>" + effect "</td></tr>" );
  // copy of the #table_body original code
   $("#table_body").append("<tr><td>" + english +"</td><td>"+cebuano + "</td><td>" +pronunciation+
                            "</td><td>" + pos + "</td><td>" + picture + "</td><td>" + audio + "</td><td>"+ effect + "</td></tr>" );


  });

  //USER DATA TABLE FETCH DATA FROM FIREBASE

  usersRef.on("child_added", snap =>{
    var email = snap.child("email").val();
    var password = snap.child("password").val();
    var type = snap.child("type").val();
    var username = snap.child("username").val();
    // var password = snap.child("password").val();

    // if(type == 2) {
    //   $("#table_premium").append("<tr><td>" + username + "</td><td>" + email + "</td></tr>");    
    // } else {
    //   $("#table_free").append("<tr><td>" + username + "</td><td>" + email + "</td></tr>");    
    // }

     $("#table_free").append("<tr><td>" + username + "</td><td>" + email + "</td><td>" + password +"</td></tr>"); 


  });
