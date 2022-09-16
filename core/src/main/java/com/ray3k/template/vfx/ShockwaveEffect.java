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

public class ShockwaveEffect extends ShaderVfxEffect implements ChainVfxEffect {
    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_TIME = "u_time";
    private static final String U_DURATION = "u_duration";
    private static final String U_RADIUS = "u_radius";
    private static final String U_THICKNESS = "u_thickness";
    private static final String U_POSITION = "u_position";
    private static final String U_VIEWPORT = "u_viewport";
    
    private final Vector2 position = new Vector2(.5f, .5f);
    private final Vector2 viewport = new Vector2();
    private float duration = 2f;
    private float radius = 200f;
    private float thickness = .4f;
    private float time = 0f;
    private boolean looping = false;

    public ShockwaveEffect() {
        super(VfxGLUtils.compileShader(Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"), Gdx.files.internal("shaders/shockwave.frag")));
        rebind();
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        if (looping && time > duration) setTime((this.time + delta) % duration);
        else setTime(this.time + delta);
    }
    
    public float getTime() {
        return time;
    }
    
    public void setTime(float time) {
        this.time = time;
        setUniform(U_TIME, time);
    }
    
    public float getDuration() {
        return duration;
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
        setUniform(U_DURATION, duration);
    }
    
    public float getThickness() {
        return thickness;
    }
    
    public void setThickness(float thickness) {
        this.thickness = thickness;
        setUniform(U_THICKNESS, thickness);
    }
    
    public float getRadius() {
        return radius;
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        setUniform(U_POSITION, position);
    }
    
    public boolean isLooping() {
        return looping;
    }
    
    public void setLooping(boolean looping) {
        this.looping = looping;
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
        program.setUniformf(U_TIME, time);
        program.setUniformf(U_DURATION, duration);
        program.setUniformf(U_RADIUS, radius);
        program.setUniformf(U_THICKNESS, thickness);
        program.setUniformf(U_VIEWPORT, viewport);
        program.setUniformf(U_POSITION, position);
        
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
