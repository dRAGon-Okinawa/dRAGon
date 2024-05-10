<template>
    <div class="card">
        <h3>Documentation</h3>
        <h5>Getting Started</h5>
        <pre class="app-code"><code> npm install </code></pre>

        <p>or</p>

        <pre class="app-code"><code> yarn </code></pre>

        <pre class="app-code"><code> npm run dev </code></pre>

        <h5>Structure</h5>
        <ul class="line-height-3">
            <li><span class="text-primary font-medium">src/layout</span>: Main layout files, needs to be present</li>
            <li><span class="text-primary font-medium">src/views</span>: Demo pages</li>
            <li><span class="text-primary font-medium">public/demo</span>: Assets used in demos</li>
            <li><span class="text-primary font-medium">public/layout</span>: Assets used in layout</li>
            <li><span class="text-primary font-medium">src/assets/demo</span>: Styles used in demos</li>
            <li><span class="text-primary font-medium">src/assets/layout</span>: SCSS files of the main layout</li>
        </ul>

        <h5>Menu</h5>
        <p>Main menu is defined at <span class="text-primary font-medium">src/layout/AppMenu.vue</span> file.</p>

        <h5>Integration with Existing Vite Applications</h5>
        <p>Only the folders that are related to the layout needs to move in to your project. We've created a short tutorial with details.</p>

        <div class="video-container">
            <iframe className="video" width="560" height="315" src="https://www.youtube.com/embed/AHeSjJFR3ZE" frameborder="0" allowfullscreen></iframe>
        </div>

       
        <h6>src/layout/_variables.scss</h6>
        <pre class="app-code">
            
 /* General */
 <br />
 $scale:14px; /* main font size */
 <br />
 $borderRadius:12px; /* border radius of layout element e.g. card, sidebar */
 <br />
 $transitionDuration:.2s; /* transition duration of layout elements e.g. sidebar, overlay menus */
        </pre>
    </div>
</template>

<style lang="scss" scoped>
@media screen and (max-width: 991px) {
    .video-container {
        position: relative;
        width: 100%;
        height: 0;
        padding-bottom: 56.25%;

        iframe {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
        }
    }
}
</style>
