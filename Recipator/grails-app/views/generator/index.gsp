<%--
  Created by IntelliJ IDEA.
  User: tbl
  Date: 24.11.15
  Time: 16:55
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>Recipator</title>

    <link href="${resource(dir: 'stylesheets', file: 'bootstrap.css')}" rel="stylesheet">
    <link href="${resource(dir: 'stylesheets', file: 'narrow-jumbotron.css')}" rel="stylesheet">

    <script type="text/javascript" src="${resource(dir: 'javascripts', file: 'jquery-2.2.0.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'javascripts', file: 'bootstrap.min.js')}"></script>

    <style>

    </style>
</head>

<body>
<div class="container">
    <div class="header clearfix">
        <h1>Recipator!</h1>
    </div>
    <a href="#" style="padding-left:100px;"><img src="${resource(dir: 'images', file: 'logo.png')}"></a>
    <div class="jumbotron" style="width:500px;height:800px;margin-top:25px;">
        <div>
            <form class="form-inline">
                <div class="input_fields_wrap form-group">
                    <button class="btn btn-default add_field_button" style="margin-bottom:5px;margin-left:0px"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add More Ingredients</button>
                    <div>

                    </div>
                </div>
                <br/>
                <div style="margin-left:0px;margin-top:20px">
                    <button  class="btn btn-primary" id="supriseButton"><span class="glyphicon glyphicon-random" aria-hidden="true"></span><div>Surprise me!</div></button>
                </div>
            </form>
        </div>
        <div id="result">
        </div>
    </div>
</div>
</body>


<script>
    $(document).ready(function() {
        var max_fields      = 10; //maximum input boxes allowed
        var wrapper         = $(".input_fields_wrap"); //Fields wrapper
        var add_button      = $(".add_field_button"); //Add button ID

        var surprise = $(".btn-primary");

        var x = 1; //initlal text box count
        $(add_button).click(function(e){ //on add input button click
            e.preventDefault();
            if(x < max_fields){ //max input box allowed
                x++; //text box increment
                $(wrapper).append('<div><label for="ingredient[]">Ingredient</label> <input type="text" class="form-control" id="ingredient[]"/> <a href="#" class="remove_field">Remove</a></div>'); //add input box

            }
        });

        $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
            e.preventDefault(); $(this).parent('div').remove(); x--;
        });
        surprise.on("click", function (e) {
            var ingredients = new Array;
            $("input[type='text']").each(function () {
               ingredients.push($(this).val());
            });
            getRecipes(ingredients);
            e.preventDefault();
        });
    });

    function getRecipes(ingredients) {
        var ajax = $.ajax({
            type : "GET",
            url: "${g.createLink(controller:'generator',action:'getNewRecipe')}",
            data: {
                "ingredients": ingredients,
            },
            dataType : "json",
            success: function (data) {
                $("#result").empty();
                $("#result").append('<ol id="lists"></ol>');
                var steps = data.recipe;
                for (var i =0; i < steps.length; i ++) {
                    $("#lists").append('<li>' + steps[i].action + " "+steps[i].ingredient +' </li>');
                }
            },
            error: function (xhr) {
                alert(xhr.responseText); //<----when no data alert the err msg
            }
        });
        return ajax;
    }
</script>
</html>