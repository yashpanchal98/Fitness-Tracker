import React from 'react';
import { useAuth } from 'react-oidc-context';
import { ArrowRight, Flame, Target, Zap } from 'lucide-react';

export default function Home() {
  const auth = useAuth();

  return (
    <div className="container animate-fade-in" style={{ marginTop: '48px' }}>
      <div style={{ textAlign: 'center', maxWidth: '800px', margin: '0 auto 64px auto' }}>
        <h1 style={{ fontSize: '4rem', marginBottom: '24px', background: 'linear-gradient(to right, #6366f1, #10b981)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
          Next-Gen Fitness Tracking
        </h1>
        <p style={{ fontSize: '1.2rem', color: 'var(--text-secondary)', marginBottom: '32px', lineHeight: '1.6' }}>
          Seamlessly sync your workouts, track your progress, and get AI-powered recommendations. 
          Built on a highly scalable microservice architecture.
        </p>
        <div style={{ display: 'flex', gap: '16px', justifyContent: 'center' }}>
          <button className="btn-primary" style={{ padding: '16px 32px', fontSize: '1.1rem' }} onClick={() => void auth.signinRedirect()}>
            Log In <ArrowRight size={20} />
          </button>
          <button className="btn-secondary" style={{ padding: '16px 32px', fontSize: '1.1rem', background: 'rgba(255,255,255,0.05)' }} onClick={() => void auth.signinRedirect({ extraQueryParams: { kc_action: 'register' } })}>
            Create Account
          </button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px' }}>
        <div className="glass-panel" style={{ padding: '32px', textAlign: 'center' }}>
          <div style={{ background: 'rgba(99, 102, 241, 0.1)', width: '64px', height: '64px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 24px auto' }}>
            <Flame color="#6366f1" size={32} />
          </div>
          <h3 style={{ marginBottom: '16px', fontSize: '1.5rem' }}>Track Everything</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Log activities, duration, calories burned, and custom metrics with ease.</p>
        </div>

        <div className="glass-panel" style={{ padding: '32px', textAlign: 'center' }}>
          <div style={{ background: 'rgba(16, 185, 129, 0.1)', width: '64px', height: '64px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 24px auto' }}>
            <Zap color="#10b981" size={32} />
          </div>
          <h3 style={{ marginBottom: '16px', fontSize: '1.5rem' }}>Event-Driven</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Real-time synchronization across services using RabbitMQ messaging.</p>
        </div>

        <div className="glass-panel" style={{ padding: '32px', textAlign: 'center' }}>
          <div style={{ background: 'rgba(245, 158, 11, 0.1)', width: '64px', height: '64px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 24px auto' }}>
            <Target color="#f59e0b" size={32} />
          </div>
          <h3 style={{ marginBottom: '16px', fontSize: '1.5rem' }}>AI Recommendations</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Get tailored workout suggestions powered by artificial intelligence.</p>
        </div>
      </div>
    </div>
  );
}
