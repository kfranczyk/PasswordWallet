
const groupBackgroundStyle = getComputedStyle(document.documentElement).getPropertyValue('--blue-darker');

//linear-gradient(0deg, rgba(255, 0, 0, 0.5), rgba(255, 0, 0, 0.5)),
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
                                <div class="orange-medium">Title</div>
                                <div class="orange-medium">Site</div>
                                <div class="orange-medium">Password</div>
                                <div class="orange-medium">More</div>
                            </nav>
                        <article>`;

        let bodyElem = document.getElementsByTagName("BODY")[0];
        bodyElem.insertAdjacentHTML("beforeend", groupBody);




        passGroup.passwords.forEach( (passElem,indxElem) => {
            let articleObj = document.querySelector(`#group_${groupIndx}`);

            let postBody = `
            <section id="${groupIndx}_${indxElem}" class="password-content-short">
                <div id="${groupIndx}_${indxElem}_name">${passElem.passwTitle}</div>
                <div id="${groupIndx}_${indxElem}_site">${passElem.passwWebAdd}</div>
                <input type="password" value="**********" id="${groupIndx}_${indxElem}_pass" class="passw-input" disabled="true">
                <div class="btn-container">
                <button class="btn btn-delete" id="${groupIndx}_${indxElem}_btn_delete"></button>
                
                <form method="POST" action="/editStoredPasswordForm" style="display: inline;">
                    <input type="hidden" name="passwEncrypted" value="${passElem.passwEncrypted}">
                    <input type="hidden" name="passwTitle" value="${passElem.passwTitle}">
                    <input type="hidden" name="passwWebAdd" value="${passElem.passwWebAdd}">
                    <input type="hidden" name="passwDescription" value="${passElem.passwDescription}">
                    <input type="hidden" name="categoryName" value="${passGroup.title}">
                    <button type="submit" class="btn btn-edit" id="${groupIndx}_${indxElem}_btn_edit"></button>
                </form>
                <button class="btn btn-copy" id="${groupIndx}_${indxElem}_btn_copy"></button>
                <button class="btn btn-show" id="${groupIndx}_${indxElem}_btn_show"></button>
                <button class="btn btn-more" id="${groupIndx}_${indxElem}_btn_more"></button>
                </div>
                <div id="${groupIndx}_${indxElem}_desc_title" class="dark-blue"></div>
                <div id="${groupIndx}_${indxElem}_desc" class="desc-div"></div>
            </section>`;


            articleObj.insertAdjacentHTML("beforeend", postBody);

        });//end of passwords


        let buttons = document.querySelectorAll(`#group_${groupIndx} .btn`);

            buttons.forEach(button => {

                button.addEventListener('click' , (event) =>{
                    let element = event.target;
                    let groupId = element.id.split("_")[0];
                    let elId = element.id.split("_")[1];
                
                    let passwElem = document.getElementById(`${groupIndx}_${elId}_pass`);
                
                    //show encrypted password
                    if(element.classList.contains("btn-show")){  
                        element.classList.remove("btn-show");
                        element.classList.add("btn-show-active");
                        passwElem.type="text";
                        passwElem.value=data[groupId].passwords[elId].passwEncrypted;
                    
                    //hide encrypted password
                    }else if(element.classList.contains("btn-show-active")){
                        element.classList.remove("btn-show-active");  
                        element.classList.add("btn-show");
                        passwElem.type="password";
                        passwElem.value="**********";

                    //copy decrypted password to clipboard
                    }else if(element.classList.contains("btn-copy")){                    
                        fetch(`/getEntry/`,{ method:"POST",
                            headers: {'Accept': 'application/json',
                                     'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({  "passTitle":`${data[groupId].passwords[elId].passwTitle}`,
                                                    "urlPassword":`${data[groupId].passwords[elId].passwEncrypted}`,
                                                    "categoryTitle":`${data[groupId].title}`})
                        })
                        .then(response=>{return response.json();})
                        .then(data=>{
                            alert("Copied the password: " + data.decodedPass);
                        });
                    
                    //show description
                    }else if(element.classList.contains("btn-more")){
                        element.classList.remove("btn-more");  
                        element.classList.add("btn-more-rot");

                        let descTitleObj = document.getElementById(`${groupIndx}_${elId}_desc_title`);
                        let descObj = document.getElementById(`${groupIndx}_${elId}_desc`);
                        descTitleObj.innerText="Description:";
                        descObj.innerText=data[groupId].passwords[elId].passwDescription;
                    
                    //hide description
                    }else if(element.classList.contains("btn-more-rot")){
                        element.classList.remove("btn-more-rot");  
                        element.classList.add("btn-more");
                    
                        let descTitleObj = document.getElementById(`${groupIndx}_${elId}_desc_title`);
                        let descObj = document.getElementById(`${groupIndx}_${elId}_desc`);
                        descTitleObj.innerText="";
                        descObj.innerText="";
                    
                    }else if(element.classList.contains("btn-delete")){
                        
                        let titleElem = document.getElementById(`${groupIndx}_${elId}_name`);

                        let answer = prompt("Please enter Password title to delete:", "");
                            if (answer === titleElem.innerText){
                                deletePassFetch()
                            }else if (answer == null || answer == "") 
                                alert("User cancelled the prompt.");
                            else 
                                alert("invalid data");
                    }




                    function deletePassFetch(){
                        fetch(`/deletePassword/`,{
                            method:"DELETE",
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({  "passTitle":`${data[groupId].passwords[elId].passwTitle}`,
                                                    "urlPassword":`${data[groupId].passwords[elId].passwEncrypted}`,
                                                    "categoryTitle":`${data[groupId].title}`})
                        })
                        .then(response=>{return response.text();})
                        .then(text=>{
                            alert(text);
                        });
                    }
                });
            
            });


    });//end of groups

    

    function hexTorgb(hexCode){
        let r = parseInt(hexCode.slice(1, 3), 16),
            g = parseInt(hexCode.slice(3, 5), 16),
            b = parseInt(hexCode.slice(5, 7), 16);
        
        return [r,g,b];
    }
});//end of fetch
