<script lang="ts">
  import favicon  from '$assets/favicon.png';
  import Nav from "$components/nav.svelte";
  import Modal from "$components/modal.svelte";
  import Spinner from '$components/Spinner.svelte';
  import Form from '$components/Form.svelte';
  import FormError from '$components/formError.svelte';

  import { redirect } from '$utils/redirect';
	import { adminSelectedUserStore} from '$stores/adminSelectedUserStore';
  import { addToastByRes } from '$utils/addToToastStore';
  import { Validators} from "$utils/Validators";
  import { fetching } from '$utils/fetching';
  import { formFormat } from '$types/formFormat';
  
  $: $adminSelectedUserStore = selectedUser;

  let users = [];
  let permissions = [];

  let showCreateModal = false;
  let showEditModal = false;
  
  let selectedUser = null;
  
  let searchUsername = "";
  let searchEmail = "";
  let searchPermission = "";

  let buttons = [
    { text: "Home", href: "/" },
    { text: "Admin", href: "/admin"},
  ];



  $: getAllUser(); 
  $: getAllPermission();

  async function getAllUser(){
    let res = await fetching("api/get-all-users", "GET");
    if(res.success) users = res.items;
    else addToastByRes(res);
  }

  async function getAllPermission(){
    let res = await fetching ("api/get-all-permissions", "GET");
    if(res.success) permissions = res.items;
    else addToastByRes(res);
    
  }

  let formValidators = {
    username: {
      validators: [Validators.required],
    },
    email: {
      validators: [Validators.required, Validators.email],
    },
    password: {
      validators: [Validators.required, Validators.minLength(8)],
    },
    permissions: {
      validators: [Validators.required],
    },
  };
  


  async function handleCreatePostFetch(){
    showCreateModal = false, 
    await getAllUser();
  }

  async function handleUpdatePostFetch(){
    showEditModal = false, 
    await getAllUser();
  }

  async function handleDeletePreFetch(e){
    const formData = new FormData(e.detail.target);
    if(formData.get("permissions").toString().includes("ADMIN")){
      if(!confirm(`Are you sure you want to delete ${formData.get("username").toString()}?`)) return;
    }
    // 
  }


</script>

<svelte:head>
	<link rel="icon" type="image/png" href={favicon}/>
	<title>Admin</title>
  <script src="http://localhost:35729/livereload.js"></script>
</svelte:head>

<Nav title="Admin" {buttons}/>
{#if showCreateModal}
    <Modal open={showCreateModal} on:close={()=>showCreateModal=false} closeOnBodyClick={false}>
        <h1 class="flex justify-center text-2xl font-bold">Create User</h1>
        <br class="pt-4"/>
        <Form url="/api/create-user" method="POST" dataFormat={formFormat.FORM} {formValidators} on:postFetch={handleCreatePostFetch}>
            <div class="flex flex-col gap-1">
              <div class="form-control">
                  <label class="input-group">
                    <span class="w-36">Username</span>
                    <input name="username" type="text" placeholder="Max" class="input input-bordered w-full" />
                  </label>
                  <FormError name="username" key="required" message="Username is required"/>

              </div>
              <br class="pt-4"/>
              <div class="form-control">
                  <label class="input-group">
                    <span class="w-36">Email</span>
                    <input name="email" type="email" placeholder="test@example" class="flex input input-bordered w-full" />
                  </label>
                  <FormError name="email" key="required" message="Email is required"/>
                  <FormError name="email" key="email" message="Email is not valid"/>
              </div>
              <br class="pt-4"/>
              <div class="form-control">
                  <label class="input-group">
                    <span class="w-36">Password</span>
                    <input name="password" type="password" placeholder="1234" class="input input-bordered w-full" />
                  </label>
                  <FormError name="password" key="required" message="Password is required"/>
                  <FormError name="password" key="minLength" message="Password must be at least 8 characters"/>
              </div>
              <br class="pt-4"/>
              <div class="form-control">
                <label class="input-group min-h-fit">
                  <span class="w-36 min-h-fit">Role</span>
                  <select name="permissions" class="flex input w-full" required>
                    {#each permissions as permission}
                      {#if permission === "USER"}
                        <option selected>{permission}</option>
                      {:else}
                        <option>{permission}</option>
                      {/if}
                    {/each}
                  </select>
                </label>
              </div>
              <br class="pt-4"/>
              <div class="flex justify-between">
                <button type="submit" class="btn btn-primary">Register</button>
                <input type="reset" class="btn btn-primary" value="Clear"/>
                <button class="btn btn-primary" on:click={()=> showCreateModal = false}>Close</button>
              </div>
            </div>
          </Form>
    </Modal>
{/if}

{#if showEditModal}
  <Modal open={showEditModal} on:close={()=>showEditModal=false} closeOnBodyClick={false}>
      <h1 class="flex justify-center text-2xl font-bold">Edit User</h1>
      <br class="pt-4"/>
      <Form url="/api/update-user" method="POST" dataFormat={formFormat.FORM} {formValidators} on:postFetch={handleUpdatePostFetch} >
        <input name="personId" type="hidden" bind:value={selectedUser.personId} required>
        <div class="flex flex-col gap-1">
          <div class="form-control">
              <label class="input-group">
              <span class="w-36">Username</span>
              <input bind:value={selectedUser.username} name="username" type="text" placeholder="Max" class="input input-bordered w-full" />
              </label>
              <FormError name="username" key="required" message="Username is required"/>
          </div>
          <br class="pt-4"/>
          <div class="form-control">
              <label class="input-group">
              <span class="w-36">Email</span>
              <input bind:value={selectedUser.email} name="email" type="text" placeholder="google@gmail.com" class="input input-bordered w-full" />
              </label>
              <FormError name="email" key="required" message="Email is required"/>
              <FormError name="email" key="email" message="Email is not valid"/>
          </div>
          <br class="pt-4"/>
          <div class="form-control">
            <label class="input-group">
              <span class="w-36">Password</span>
              <input name="password" class="input input-bordered w-full" type="password">
            </label>
            <FormError name="password" key="required" message="Password is required"/>
            <FormError name="password" key="minLength" message="Password must be at least 8 characters"/>
          </div>
          <br class="pt-4"/>
          <div class="form-control">
            <label class="input-group">
              <span class="w-36">Role</span>
              <select name="permissions" class="flex input w-full">
                {#each permissions as permission}
                  {#if selectedUser.permissions.includes(permission)}
                    <option selected>{permission}</option>
                  {:else}
                    <option>{permission}</option>
                  {/if}
                {/each}
              </select>
            </label>
          </div>
          <br class="pt-4"/>
          <div class="flex justify-between">
            <button type="submit" class="btn btn-primary">Update</button>
            <input type="reset" class="btn btn-primary" value="Clear"/>
            <button class="btn btn-primary" on:click={()=> showEditModal = false}>Close</button>
          </div>
        </div>
      </Form>
  </Modal>
{/if}

<main class="mt-20 m-8 flex-justify-center">
  
  <br class="mt-20"/>
  <div class="overflow-x-auto z-0">
      <table class="table table-zebra table-compact w-full z-0">
        <thead class="z-0">
          <tr>
            <th>Search</th>
            <th><input bind:value={searchUsername} class="input bg-slate-900" placeholder="Username"/></th>
            <th><input bind:value={searchEmail} class="input bg-slate-900" placeholder="Email"/></th>
            <th><input bind:value={searchPermission} class="input bg-slate-900" placeholder="ADMIN"/></th>
            <th><button class="btn btn-primary" on:click={()=> showCreateModal = true}>Create User</button></th>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <thead class="bg-slate-900">
          <tr>
            <th>Id</th>
            <th>Username</th>
            <th>email</th>
            <th>Roles</th>
            <th>showDecks</th>
            <th>edit</th>
            <th>remove</th>
          </tr>
        </thead>
        <tbody>
          {#if users.length === 0}
            <Spinner/>
          {:else}
          {#key users}
              {#each users as user}
                  {#if user.username.toLowerCase().includes(searchUsername.toLowerCase()) && user.email.toLowerCase().includes(searchEmail.toLowerCase()) && user.permissions.toString().toLowerCase().includes(searchPermission.toLowerCase())}
                        <tr>
                          <td><Form url="/api/delete-user" method="DELETE" dataFormat={formFormat.FORM} id={user.personId} on:preFetch={handleDeletePreFetch} on:postFetch={async () => await getAllUser()}/>{user.personId.slice(0,5)+"..."}</td>
                          <input type="hidden" form={user.personId} bind:value={user.personId} name="personId"/>
                          <td><input form={user.personId} type="text" name="username" bind:value={user.username} class="bg-transparent" readonly/></td>
                          <td><input form={user.personId} type="text" name="email" bind:value={user.email} class="bg-transparent" readonly/></td>
                          <td><input form={user.personId} type="text" name="permissions" bind:value={user.permissions} class="bg-transparent" readonly/></td>
                          <td><button class="btn btn-info" on:click={()=>{$adminSelectedUserStore=user; redirect("admin/show-decks")}}>Decks</button></td>
                          <!-- svelte-ignore a11y-click-events-have-key-events -->
                          <td><button class="btn btn-secondary" on:click={()=>{showEditModal=true; selectedUser=user}}>Edit</button></td>
                          <td><button class="btn btn-info" form={user.personId} type="submit">Delete</button></td>
                      </tr>
                  {/if}
              {/each}
            {/key}
          {/if}
        </tbody>
      </table>
    </div>
</main>
