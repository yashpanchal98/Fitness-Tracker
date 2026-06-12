import React, { useEffect, useState } from 'react';
import { useAuth } from 'react-oidc-context';
import { Activity as ActivityIcon, Plus, User, Clock, Flame } from 'lucide-react';
import { api } from '../services/api';

export default function Dashboard() {
  const auth = useAuth();
  const [profile, setProfile] = useState(null);
  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState(null);

  // AI Recommendation State
  const [recommendations, setRecommendations] = useState({});
  const [loadingRecs, setLoadingRecs] = useState({});
  const [expandedRecs, setExpandedRecs] = useState({});

  // New activity form state
  const [showForm, setShowForm] = useState(false);
  const [activityType, setActivityType] = useState('Running');
  const [duration, setDuration] = useState('');
  const [calories, setCalories] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const profRes = await api.get('/users/me');
        setProfile(profRes.data);

        const actRes = await api.get(`/activities/user/${profRes.data.id}`);
        setActivities(actRes.data);
      } catch (err) {
        console.error("Failed to fetch dashboard data:", err);
        setErrorMsg(err.response?.data?.message || err.message || "Unknown error occurred");
      } finally {
        setLoading(false);
      }
    };
    if (auth.isAuthenticated) {
      fetchData();
    }
  }, [auth.isAuthenticated]);

  const handleAddActivity = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/activities', {
        userId: profile.id,
        activityType,
        duration: parseInt(duration),
        caloriesBurned: parseInt(calories)
      });
      setActivities([...activities, res.data]);
      setShowForm(false);
      setDuration('');
      setCalories('');
    } catch (err) {
      console.error("Failed to save activity:", err);
    }
  };

  const handleToggleRecommendation = async (activityId) => {
    if (expandedRecs[activityId]) {
      // Just hide it
      setExpandedRecs({ ...expandedRecs, [activityId]: false });
      return;
    }

    // Show it
    setExpandedRecs({ ...expandedRecs, [activityId]: true });

    // If already loaded, do nothing
    if (recommendations[activityId]) return;

    setLoadingRecs({ ...loadingRecs, [activityId]: true });
    try {
      const res = await api.get(`/recommendation/activity/${activityId}`);
      if (res.data) {
        setRecommendations(prev => ({ ...prev, [activityId]: res.data }));
      } else {
        setRecommendations(prev => ({ ...prev, [activityId]: { error: 'No recommendation available yet.' } }));
      }
    } catch (err) {
      console.error("Failed to fetch recommendation:", err);
      setRecommendations(prev => ({ ...prev, [activityId]: { error: 'Could not load recommendation.' } }));
    } finally {
      setLoadingRecs(prev => ({ ...prev, [activityId]: false }));
    }
  };

  if (loading) {
    return <div className="container" style={{ textAlign: 'center', marginTop: '100px' }}>Loading your dashboard...</div>;
  }

  if (errorMsg || !profile) {
    return (
      <div className="container" style={{ marginTop: '48px', textAlign: 'center' }}>
        <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid #ef4444', padding: '24px', borderRadius: '12px', display: 'inline-block' }}>
          <h2 style={{ color: '#ef4444', marginBottom: '12px' }}>Oops! Something went wrong</h2>
          <p>{errorMsg || "Failed to load profile data."}</p>
          <p style={{ marginTop: '16px', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Check your gateway and user-service terminal logs for more details.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container animate-fade-in">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <h1>Welcome back, {profile?.firstName || 'Athlete'}!</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Ready to crush your goals today?</p>
        </div>
        <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
          <Plus size={20} /> Add Activity
        </button>
      </div>

      {showForm && (
        <div className="glass-panel animate-fade-in" style={{ padding: '24px', marginBottom: '32px' }}>
          <h3 style={{ marginBottom: '16px' }}>Log New Activity</h3>
          <form onSubmit={handleAddActivity} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr auto', gap: '16px', alignItems: 'end' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)' }}>Type</label>
              <select value={activityType} onChange={e => setActivityType(e.target.value)} required>
                <option value="Running">Running</option>
                <option value="Cycling">Cycling</option>
                <option value="Swimming">Swimming</option>
                <option value="Weightlifting">Weightlifting</option>
                <option value="Yoga">Yoga</option>
              </select>
            </div>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)' }}>Duration (mins)</label>
              <input type="number" value={duration} onChange={e => setDuration(e.target.value)} required min="1" placeholder="30" />
            </div>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)' }}>Calories</label>
              <input type="number" value={calories} onChange={e => setCalories(e.target.value)} required min="1" placeholder="300" />
            </div>
            <div style={{ marginBottom: '16px' }}>
              <button type="submit" className="btn-primary" style={{ height: '46px' }}>Save</button>
            </div>
          </form>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 3fr', gap: '32px' }}>
        {/* Profile Sidebar */}
        <div className="glass-panel" style={{ padding: '24px', height: 'fit-content' }}>
          <div style={{ textAlign: 'center', marginBottom: '24px' }}>
            <div style={{ background: 'var(--primary-accent)', width: '80px', height: '80px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px auto', fontSize: '2rem', fontWeight: 'bold' }}>
              {profile?.firstName?.charAt(0) || <User size={40} />}
            </div>
            <h3 style={{ fontSize: '1.2rem' }}>{profile?.firstName} {profile?.lastName}</h3>
            <p style={{ color: 'var(--text-secondary)' }}>{profile?.email}</p>
          </div>
          <div style={{ borderTop: '1px solid var(--surface-border)', paddingTop: '16px' }}>
            <p style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Total Activities</span>
              <strong>{activities.length}</strong>
            </p>
            <p style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Member Since</span>
              <strong>{new Date(profile?.createdAt).toLocaleDateString()}</strong>
            </p>
          </div>
        </div>

        {/* Activity Feed */}
        <div className="glass-panel" style={{ padding: '24px' }}>
          <h3 style={{ marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <ActivityIcon size={24} color="var(--primary-accent)" /> 
            Recent Activities
          </h3>

          {activities.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '48px 0', color: 'var(--text-secondary)' }}>
              <ActivityIcon size={48} opacity={0.5} style={{ margin: '0 auto 16px auto' }} />
              <p>No activities found. Start tracking your fitness journey!</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {activities.map(act => (
                <React.Fragment key={act.id}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px', background: 'rgba(255, 255, 255, 0.03)', borderRadius: '12px', border: '1px solid var(--surface-border)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                    <div style={{ background: 'rgba(16, 185, 129, 0.1)', padding: '12px', borderRadius: '12px' }}>
                      <ActivityIcon color="#10b981" size={24} />
                    </div>
                    <div>
                      <h4 style={{ fontSize: '1.1rem', marginBottom: '4px' }}>{act.activityType}</h4>
                      <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                        {new Date(act.timestamp).toLocaleString()}
                      </p>
                    </div>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'flex-end' }}>
                    <div style={{ display: 'flex', gap: '24px', textAlign: 'right' }}>
                      <div>
                        <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '4px', justifyContent: 'flex-end' }}><Clock size={14} /> Duration</p>
                        <strong>{act.duration} min</strong>
                      </div>
                      <div>
                        <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '4px', justifyContent: 'flex-end' }}><Flame size={14} /> Calories</p>
                        <strong>{act.caloriesBurned}</strong>
                      </div>
                    </div>
                    <button 
                      onClick={() => handleToggleRecommendation(act.id)}
                      className="btn-secondary" 
                      style={{ padding: '6px 12px', fontSize: '0.8rem', marginTop: '8px' }}
                    >
                      {expandedRecs[act.id] ? 'Hide AI Advice' : 'Ask AI'}
                    </button>
                  </div>
                </div>
                
                {/* AI Recommendation Panel */}
                {expandedRecs[act.id] && (
                  <div className="animate-fade-in" style={{ padding: '16px', background: 'rgba(59, 130, 246, 0.1)', border: '1px solid rgba(59, 130, 246, 0.3)', borderRadius: '12px', marginTop: '-8px', marginBottom: '8px' }}>
                    <h4 style={{ color: '#60a5fa', marginBottom: '8px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                      ✨ AI Recommendation
                    </h4>
                    {loadingRecs[act.id] ? (
                      <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Generating insights...</p>
                    ) : recommendations[act.id]?.error ? (
                      <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>{recommendations[act.id].error}</p>
                    ) : recommendations[act.id] ? (
                      <div style={{ fontSize: '0.9rem', color: 'var(--text-primary)' }}>
                        <p style={{ marginBottom: '12px' }}><strong>Feedback:</strong> {recommendations[act.id].recommendation}</p>
                        
                        {recommendations[act.id].improvements?.length > 0 && (
                          <div style={{ marginBottom: '8px' }}>
                            <strong style={{ color: '#10b981' }}>Areas for Improvement:</strong>
                            <ul style={{ paddingLeft: '20px', marginTop: '4px' }}>
                              {recommendations[act.id].improvements.map((imp, idx) => <li key={idx}>{imp}</li>)}
                            </ul>
                          </div>
                        )}

                        {recommendations[act.id].safety?.length > 0 && (
                          <div style={{ marginBottom: '8px' }}>
                            <strong style={{ color: '#f59e0b' }}>Safety Tips:</strong>
                            <ul style={{ paddingLeft: '20px', marginTop: '4px' }}>
                              {recommendations[act.id].safety.map((s, idx) => <li key={idx}>{s}</li>)}
                            </ul>
                          </div>
                        )}
                        
                        {recommendations[act.id].suggestions?.length > 0 && (
                          <div>
                            <strong style={{ color: '#8b5cf6' }}>Next Steps:</strong>
                            <ul style={{ paddingLeft: '20px', marginTop: '4px' }}>
                              {recommendations[act.id].suggestions.map((s, idx) => <li key={idx}>{s}</li>)}
                            </ul>
                          </div>
                        )}
                      </div>
                    ) : null}
                  </div>
                )}
              </React.Fragment>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
