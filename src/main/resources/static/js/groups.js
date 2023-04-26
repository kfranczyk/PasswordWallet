const groupBackgroundStyle = getComputedStyle(document.documentElement).getPropertyValue('--blue-darker');

fetch('/getEntries',{ method:"POST"})
.then(response=>{return response.json();})
.then(data=>{
    console.log(data);
    
    data.forEach( (passGroup,groupIndx) =>{
        let rgb = hexTorgb(passGroup.color);
        let gradientStyle = `background: linear-gradient(0deg, rgba(${rgb[0]},${rgb[1]},${rgb[2]}, 0.5), rgba(${rgb[0]},${rgb[1]},${rgb[2]}, 0.5) ),${groupBackgroundStyle}`;

        let groupBody = `
                <article id="group_${groupIndx}">
                    <nav id="group_${groupIndx}_nav" class="content-nav nav-font" style="${gradientStyle}">
                        <div id="group_${groupIndx}_title" class="orange-medium group-title">${passGroup.title}</div>
                    </nav>
                    <form action="/editGroup/${passGroup.title}"  method="GET" class="group-form">
                        <label for="groupTitle_${groupIndx}" class="label-medium-blue">Group title<span class="span-red">*</span>:</label>
                        <input type="text" id="groupTitle_${groupIndx}" name="title" class="font-fix typein-input" required  pattern="^([\\wąłęźćóżńśĄŁĘŹĆÓŻŃŚ\\-_#\\(\\)]{3,45})$">

                        <label for="groupColor_${groupIndx}" class="label-medium-blue">Color<span class="span-red">*</span>:</label>
                        <input type="color" id="groupColor_${groupIndx}" name="color" value="${passGroup.color}" class="font-fix typein-input" style="padding:1px 2px">

                        <input id="submit_${groupIndx}" type="submit" value="submit" class="form-button font-fix">
                        <input id="delete_${passGroup.title}" type="button" value="delete" class="form-button font-fix delete">
                    </form>
                </article>`;

        let bodyElem = document.getElementsByTagName("BODY")[0];
        bodyElem.insertAdjacentHTML("beforeend", groupBody);

        
    });

    

    let buttonElements = document.querySelectorAll(".delete");
    buttonElements.forEach( (buttonItem) =>{
        
        buttonItem.addEventListener("click", (e) =>{
            let groupName = e.target.id;
            console.log(groupName);
            groupName = groupName.split("_");
            let res = e.target.id.substring(7);
            let answer = prompt("Please enter group name to delete:", "");
                if (answer === res){
                    fetch(`/deleteEntry/${res}`,{method:`DELETE`})
                        .then(response =>{ return response.text();})
                        .then(data =>{alert(`Komunikat z serwera:${data}`)});
                }else if (answer == null || answer == "") 
                    alert("User cancelled the prompt.");
                else 
                    alert("invalid data");
        });
    });
    
    
});

function hexTorgb(hexCode){
    let r = parseInt(hexCode.slice(1, 3), 16),
        g = parseInt(hexCode.slice(3, 5), 16),
        b = parseInt(hexCode.slice(5, 7), 16);
    
    return [r,g,b];
}

/*
let myForm = document.querySelectorAll("#delete");
        let buttonElem = document.querySelector("#submit");
        buttonElem.addEventListener("click", () =>{
        let person = prompt("Please enter your name:", "dupa");
        if (person == null || person == "") {
                alert("User cancelled the prompt.");
            } else {
                myForm.requestSubmit();
            }
        });
*/
let colorInput = document.querySelector("#groupColor");
colorInput.onchange = () =>{
    let rgb = hexTorgb(colorInput.value);
    let gradientStyle = `linear-gradient(0deg, rgba(${rgb[0]},${rgb[1]},${rgb[2]}, 0.5), rgba(${rgb[0]},${rgb[1]},${rgb[2]}, 0.5) ),${groupBackgroundStyle}`;
    let testNav = document.querySelector("#group_test_nav");
    testNav.style.background = gradientStyle;
};

