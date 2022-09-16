/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ray3k.template.vfx;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.ShaderVfxEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class PixelateEffect extends ShaderVfxEffect implements ChainVfxEffect {
    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_PIXEL_SIZE = "u_pixelSize";
    private static final String U_VIEWPORT = "u_viewport";
    
    private final Vector2 pixelSize = new Vector2(25f, 25f);
    private final Vector2 viewport = new Vector2();

    public PixelateEffect() {
        super(VfxGLUtils.compileShader(Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"), Gdx.files.internal("shaders/pixelate.frag")));
        rebind();
    }
    
    public Vector2 getPixelSize() {
        return pixelSize;
    }
    
    public void setPixelSize(Vector2 pixelSize) {
        setPixelSize(pixelSize.x, pixelSize.y);
    }
    
    public void setPixelSize(float x, float y) {
        pixelSize.set(x, y);
        setUniform(U_PIXEL_SIZE, pixelSize);
    }
    
    public Vector2 getViewport() {
        return viewport;
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.set(width, height);
        setUniform(U_VIEWPORT, viewport);
    }
    
    @Override
    public void rebind() {
        super.rebind();
        program.bind();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_PIXEL_SIZE, pixelSize);
        program.setUniformf(U_VIEWPORT, viewport);
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        render(context, buffers.getSrcBuffer(), buffers.getDstBuffer());
    }
    
    public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
        // Bind src buffer's texture as a primary one.
        src.getTexture().bind(TEXTURE_HANDLE0);
        // Apply shader effect and render result to dst buffer.
        renderShader(context, dst);
    }
}
