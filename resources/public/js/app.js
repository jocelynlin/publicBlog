$(document).ready(main);

var main = function(){
  $('.next').click(function(){
    window.location = "{{servlet-context}}/page/2";
  });
};
