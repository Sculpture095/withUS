document.addEventListener("DOMContentLoaded", function(){
    const customSelects = document.querySelectorAll(".custom-select");

    /**
     * 선택된 옵션(.option.active)을 모아서
     * 1) hidden input(.tags_input)에 콤마로 연결하여 저장
     * 2) .selected-options 영역에 <span class="tag">로 표시
     */
    function updateSelectedOptions(customSelect){
        // 'all-tags' (Select All) 제외하고 active인 옵션만 수집
        const activeOptions = Array.from(
            customSelect.querySelectorAll(".option.active")
        ).filter(option => !option.classList.contains("all-tags"))
            .map(option => ({
                value: option.getAttribute("data-value"),
                text: option.textContent.trim()
            }));

        // hidden input에 선택된 값들을 콤마로 join하여 저장
        const tagsInput = customSelect.querySelector(".tags_input");
        if(tagsInput){
            tagsInput.value = activeOptions.map(opt => opt.value).join(", ");
        }

        // 선택된 옵션들을 <span class="tag"> 구조로 표시
        let tagsHTML = "";
        if(activeOptions.length === 0){
            // 아무것도 선택되지 않았을 때 안내 문구
            tagsHTML = '<span class="placeholder">추가할 기술을 선택하세요</span>';
        } else {
            // 예: 전체 표시 (추가로 +N 형태로 묶고 싶다면 로직 수정)
            activeOptions.forEach(opt => {
                tagsHTML += `
                    <span class="tag">
                      ${opt.text}
                      <span class="remove-tag" data-value="${opt.value}">&times;</span>
                    </span>
                `;
            });
        }

        customSelect.querySelector(".selected-options").innerHTML = tagsHTML;
    }

    customSelects.forEach(function(customSelect){
        const selectBox       = customSelect.querySelector(".select-box");
        const optionsContainer= customSelect.querySelector(".options");
        const searchInput     = customSelect.querySelector(".search-tags");
        const clearButton     = customSelect.querySelector(".clear");
        const options         = customSelect.querySelectorAll(".option");
        const noResultMessage = customSelect.querySelector(".no-result-message");
        const allTagsOption   = customSelect.querySelector(".option.all-tags");

        // 1) selectBox 클릭 → .custom-select에 .open 토글 (드롭다운 열고 닫기)
        selectBox.addEventListener("click", function(event){
            // 이미 태그(×)를 누른 경우가 아니라면 열기/닫기
            if(!event.target.closest(".tag")){
                customSelect.classList.toggle("open");
            }
        });

        // 2) "Select All" 옵션 클릭 시 나머지 옵션 active 토글
        if(allTagsOption){
            allTagsOption.addEventListener("click", function(){
                const isActive = allTagsOption.classList.contains("active");
                options.forEach(function(option){
                    if(!option.classList.contains("all-tags")){
                        option.classList.toggle("active", !isActive);
                    }
                });
                updateSelectedOptions(customSelect);
            });
        }

        // 3) 검색창에 입력 시 옵션 필터링
        if(searchInput){
            searchInput.addEventListener("input", function(){
                const searchTerm = searchInput.value.toLowerCase();
                options.forEach(option => {
                    if(option.classList.contains("all-tags")) return;
                    const text = option.textContent.trim().toLowerCase();
                    option.style.display = text.includes(searchTerm) ? "block" : "none";
                });
                const anyVisible = Array.from(options).some(option =>
                    !option.classList.contains("all-tags") && option.style.display === "block"
                );
                noResultMessage.style.display = anyVisible ? "none" : "block";
            });
        }

        // 4) 옵션 클릭 → active 토글 후 updateSelectedOptions
        options.forEach(option => {
            option.addEventListener("click", function(){
                // all-tags는 별도 처리(위에서)
                if(!option.classList.contains("all-tags")){
                    option.classList.toggle("active");
                    updateSelectedOptions(customSelect);
                }
            });
        });
    });

    // 5) 태그 내의 ×(remove-tag) 클릭 시 옵션 active 해제
    document.addEventListener("click", function(event){
        if(event.target.classList.contains("remove-tag")){
            const valueToRemove = event.target.getAttribute("data-value");
            const customSelect = event.target.closest(".custom-select");
            if(customSelect){
                const option = customSelect.querySelector(`.option[data-value="${valueToRemove}"]`);
                if(option){
                    option.classList.remove("active");
                }
                updateSelectedOptions(customSelect);
            }
        }
    });

    // 6) 외부 영역 클릭 시 닫기
    document.addEventListener("click", function(event){
        if(!event.target.closest(".custom-select")){
            customSelects.forEach(function(cs){
                cs.classList.remove("open");
            });
        }
    });
});
