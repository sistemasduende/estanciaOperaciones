/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var handle={};

function on_start() {
    handle=setTimeout(function (){
        $('html').addClass('progress');
    },250);
}

function on_complete() {
    clearTimeout(handle);
    $('html').removeClass('progress');
}

