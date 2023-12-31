<script lang="ts">
	
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    import { addToastByRes } from '$utils/addToToastStore';
    import { fetching } from '$utils/fetching';
    import Markdown from '$components/markdown.svelte';
    

    export let deck; 
    let { deckId, name, description, published, blocked, numCards, numCardsToRepeat, numNotLearnedCards} = deck;
    

    let hover = false
    function handleMouseOut() {
        hover = false
    }
    function handleMouseOver() {
        hover = true
    }
    
    function handleEditDeck() {
        dispatch('editDeck');
    }

    function handleEditCards(){
        dispatch('editCards');
    }

    async function handlePublishDeck(){
        published = !published;
        if(published){
            let res = await fetching("/api/publish-deck", "POST", [{name: "deckId", value: deckId}]);
            addToastByRes(res);
        }
        if(!published){
            let res = await fetching("/api/unpublish-deck", "POST", [{name: "deckId", value: deckId}]);
            addToastByRes(res);
        }
    }

    async function handleDeleteDeck() {
        let res = await fetching("/api/delete-deck", "DELETE", [{name: "deckId", value: deckId}]);
        addToastByRes(res);
        dispatch('deleteDeck');
    }
    
    function handleListCards() {
        dispatch('listCards');
    }
    
    function handleLearnDeck(){
        dispatch('learnDeck');
    }

</script>

<svelte:head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.2/dist/katex.min.css" integrity="sha384-bYdxxUwYipFNohQlHt0bjN/LCpueqWz13HufFEV1SUatKs1cm4L6fFgCi1jT643X" crossorigin="anonymous">
</svelte:head>


<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<div class="bg-slate-900 rounded-xl shadow-xl p-5 h-96 relative overflow-clip" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut}>
        <div class="{hover ? "hidden" : "block"} max-h-full" >
            <h1 class="font-bold flex justify-center text-2xl">{name}</h1>
            <br class="pt-2"/>
            <div class="max-h-[200px] overflow-clip">
                <Markdown data={description}/>
            </div>

            <br class="pt-8"/>

            <div class="bottom-0 absolute mb-4">
                <div class="flex flex-col gap-1">
                    <div class="gap-2">
                        {#if numCards > 0}
                            <div class="badge badge-primary">Cards: {numCards} </div>
                        {:else}
                            <div class="badge badge-error">No cards</div>
                        {/if}
                        {#if numCardsToRepeat > 0}
                            <div class="tooltip" data-tip="Number of cards you have already learned once and the time came to repeat them">
                                <div class="badge badge-primary">To repeat: {numCardsToRepeat} </div>
                            </div>
                        {:else}
                            <div class="badge badge-error">Nothing to repeat</div>
                        {/if}
                    </div>
                    <div class="gap-2">
                        {#if numNotLearnedCards > 0}
                            <div class="tooltip" data-tip="Number of cards you have never learned">
                                <div class="badge badge-primary">To learn: {numNotLearnedCards} </div>
                            </div>
                        {:else}
                            <div class="badge badge-error">No cards to learn</div>
                        {/if}
                        {#if blocked}
                            <div class="tooltip" data-tip="Deck got blocked by admin therefore it will not be shown in public decks">
                                <div class="badge badge-error">Blocked by admin</div>
                            </div>
                        {:else if published}
                            <div class="tooltip" data-tip="Deck is published and can be seen by everyone">
                                <div class="badge badge-info">Published</div>
                            </div>
                        {:else} 
                            <div class="tooltip" data-tip="Deck is not published and can not be seen by everyone">
                                <div class="badge badge-error">Not Published</div>
                            </div>
                        {/if}
                    </div>
                </div>
            </div>
        </div>


        <div class="{hover ? "block" : "hidden"} grid grid-row gap-2">
            <button class="btn btn-success" on:click={handleLearnDeck}>Learn Deck</button>
            <button class="btn btn-primary" on:click={handleListCards}>List Cards</button>
            <button class="btn btn-primary" on:click={handleEditDeck}>Edit Deck</button>
            <button class="btn btn-primary" on:click={handleEditCards}>Edit Cards</button>
            <button class="btn {published ? "btn-secondary" : "btn-primary"}" on:click={handlePublishDeck}>Publish Deck</button>
            <button class="btn btn-error" on:click={handleDeleteDeck}>Delete Deck</button>
        </div>       
</div>
