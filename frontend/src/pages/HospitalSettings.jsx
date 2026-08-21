import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Settings, ArrowLeft, Plus, XCircle } from 'lucide-react';
import { getHospitalId } from '../utils/auth';

// Configuration for dynamic labels and hints
const CATEGORY_CONFIG = {
  LAB_TEST_CATEGORY: {
    label: 'Lab Test Categories',
    keyPlaceholder: 'e.g., HEMATOLOGY, SEROLOGY',
    labelPlaceholder: 'e.g., Hematology, Serology',
    hint: 'Used to group lab tests. Key must match what you select when creating a Lab Test.'
  },
  MEDICATION_CATEGORY: {
    label: 'Medication Categories',
    keyPlaceholder: 'e.g., ANALGESIC, ANTIBIOTIC',
    labelPlaceholder: 'e.g., Analgesic, Antibiotic',
    hint: 'Therapeutic groups for medications. Used in pharmacy inventory and prescribing.'
  },
  DEPARTMENT: {
    label: 'Departments',
    keyPlaceholder: 'e.g., OPD, EMERGENCY, PEDIATRICS',
    labelPlaceholder: 'e.g., Outpatient Dept, Emergency Room',
    hint: 'Hospital units where staff are assigned and patients are treated.'
  },
  TRIAGE_CATEGORY: {
    label: 'Triage Categories',
    keyPlaceholder: 'e.g., EMERGENCY, URGENT, NON_URGENT',
    labelPlaceholder: 'e.g., Emergency (Red), Urgent (Yellow)',
    hint: 'Patient priority levels. Determines how quickly a patient sees a doctor.'
  },
  BLOOD_GROUP: {
    label: 'Blood Groups',
    keyPlaceholder: 'e.g., A_POS, O_NEG, AB_POS',
    labelPlaceholder: 'e.g., A+, O-, AB+',
    hint: 'Standard blood types for patient registration and transfusion safety.'
  }
};

export default function HospitalSettings() {
  const navigate = useNavigate();
  const hospitalId = getHospitalId();
  
  // ✅ ADDED: Missing state declarations
  const [selectedCategory, setSelectedCategory] = useState('LAB_TEST_CATEGORY');
  const [settings, setSettings] = useState([]);
  const [newSetting, setNewSetting] = useState({ key: '', label: '', sortOrder: 0 });
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false); // ✅ THIS WAS MISSING

  const fetchSettings = async () => {
    try {
      const res = await api.get(`/settings?hospitalId=${hospitalId}&category=${selectedCategory}`);
      setSettings(res.data.sort((a, b) => a.sortOrder - b.sortOrder));
    } catch (err) { console.error(err); } finally { setLoading(false); }
  };

  useEffect(() => { 
    fetchSettings(); 
    setShowForm(false); // Hide form when switching categories
  }, [selectedCategory]);


  const handleDeactivate = async (id) => {
    if (window.confirm('Deactivate this setting? Existing records will keep the old value.')) {
      await api.patch(`/settings/${id}/deactivate`);
      fetchSettings();
    }
  };

  // ✅ ADD THIS STATE NEAR YOUR OTHER useState DECLARATIONS
  const [feedback, setFeedback] = useState({ type: '', message: '' }); // 'success' | 'error'
  const handleAdd = async (e) => {
    e.preventDefault();
    setFeedback({ type: '', message: '' }); // Clear previous feedback
    
    try {
        // ✅ FIX: Ensure payload matches backend expectations exactly
        const payload = {
        hospitalId: Number(hospitalId), // Ensure it's a number, not string
        category: selectedCategory,
        key: newSetting.key.trim(),
        label: newSetting.label.trim(),
        sortOrder: Number(newSetting.sortOrder) || 0
        };
        
        await api.post('/settings', payload);
        
        // ✅ SUCCESS FEEDBACK (Inline, not alert)
        setFeedback({ 
        type: 'success', 
        message: `"${newSetting.label}" added successfully to ${currentConfig.label}!` 
        });
        
        // Reset form and refresh list
        setNewSetting({ key: '', label: '', sortOrder: 0 });
        fetchSettings();
        
        // Auto-hide success message after 3 seconds
        setTimeout(() => setFeedback({ type: '', message: '' }), 3000);
        
    } catch (err) {
        // ✅ ERROR FEEDBACK (Inline, descriptive)
        const errorMsg = err.response?.data?.message 
        || err.response?.data?.error 
        || `Failed to add setting. Please check your input and try again.`;
        
        setFeedback({ type: 'error', message: errorMsg });
    }
    };

  const currentConfig = CATEGORY_CONFIG[selectedCategory];

  return (
    <div className="space-y-6">
      <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-gray-500 hover:text-primary transition text-sm">
        <ArrowLeft className="h-4 w-4" /> Back to Dashboard
      </button>

      <div className="flex items-center gap-3">
        <div className="bg-blue-100 p-2 rounded-lg"><Settings className="h-7 w-7 text-blue-600" /></div>
        <div>
          <h2 className="text-3xl font-bold text-gray-800">Hospital Settings</h2>
          <p className="text-gray-500">Manage dropdown options and configuration values</p>
        </div>
      </div>

      {/* Category Selector Tabs */}
      <div className="flex gap-2 overflow-x-auto pb-2">
        {Object.entries(CATEGORY_CONFIG).map(([key, config]) => (
          <button
            key={key}
            onClick={() => setSelectedCategory(key)}
            className={`px-4 py-2 rounded-lg text-sm font-medium whitespace-nowrap transition ${
              selectedCategory === key 
                ? 'bg-blue-600 text-white' 
                : 'bg-white border border-gray-200 text-gray-600 hover:bg-gray-50'
            }`}
          >
            {config.label}
          </button>
        ))}
      </div>

      {/* ✅ Toggle Button for Add Form */}
      {!showForm && (
        <button 
          onClick={() => setShowForm(true)}
          className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          <Plus className="h-4 w-4" /> Add New {currentConfig.label}
        </button>
      )}

      {/* Dynamic Add Form */}
            {/* Dynamic Add Form */}
      {showForm && (
        <div className="bg-blue-50 border border-blue-200 p-6 rounded-xl shadow-sm">
          <div className="flex justify-between items-center mb-4">
            <h4 className="text-base font-bold text-blue-800 flex items-center gap-2">
              <Plus className="h-5 w-5" /> Add New {currentConfig.label}
            </h4>
            <button onClick={() => setShowForm(false)} className="text-gray-400 hover:text-gray-600 transition">
              <XCircle className="h-6 w-6" />
            </button>
          </div>
          
          <form onSubmit={handleAdd} className="grid grid-cols-1 md:grid-cols-12 gap-x-6 gap-y-4 items-end">
            {/* Key Field - Spans 5 columns */}
            <div className="md:col-span-5">
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">
                Internal Key (Unique ID) <span className="text-red-500">*</span>
              </label>
              <input 
                value={newSetting.key} 
                onChange={e => setNewSetting({...newSetting, key: e.target.value.toUpperCase().replace(/[^A-Z0-9_]/g, '_')})}
                placeholder={currentConfig.keyPlaceholder} 
                required 
                // ✅ FIX: Explicitly set bg-white to override global dark styles
                className="w-full px-3 py-2.5 bg-white border border-gray-300 rounded-lg text-sm uppercase font-mono focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" 
              />
              <p className="text-[11px] text-gray-500 mt-1.5">Uppercase, no spaces. Used by system internally.</p>
            </div>

            {/* Label Field - Spans 5 columns */}
            <div className="md:col-span-5">
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">
                Display Label <span className="text-red-500">*</span>
              </label>
              <input 
                value={newSetting.label} 
                onChange={e => setNewSetting({...newSetting, label: e.target.value})}
                placeholder={currentConfig.labelPlaceholder} 
                required 
                // ✅ FIX: Explicitly set bg-white
                className="w-full px-3 py-2.5 bg-white border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" 
              />
              <p className="text-[11px] text-gray-500 mt-1.5">What users will see in dropdown menus.</p>
            </div>

            {/* Sort Order - Spans 1 column */}
            <div className="md:col-span-1">
              <label className="block text-xs font-semibold text-gray-700 mb-1.5">Order</label>
              <input 
                type="number" min="0"
                value={newSetting.sortOrder} 
                onChange={e => setNewSetting({...newSetting, sortOrder: Number(e.target.value)})}
                // ✅ FIX: Explicitly set bg-white + consistent padding
                className="w-full px-3 py-2.5 bg-white border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition" 
              />
            </div>

            {/* Submit Button - Spans 1 column, aligned to bottom */}
            <div className="md:col-span-1">
              <button type="submit" className="w-full bg-blue-600 text-white px-4 py-2.5 rounded-lg hover:bg-blue-700 active:bg-blue-800 flex items-center justify-center gap-1.5 text-sm font-medium transition shadow-sm">
                <Plus className="h-4 w-4" /> Add
              </button>
            </div>
          </form>
          {/* ✅ USER-FRIENDLY FEEDBACK BANNER */}
            {feedback.message && (
                <div className={`mt-4 p-3 rounded-lg text-sm flex items-center gap-2 transition-all duration-300 ${
                feedback.type === 'success' 
                    ? 'bg-green-50 text-green-700 border border-green-200' 
                    : 'bg-red-50 text-red-700 border border-red-200'
                }`}>
                {feedback.type === 'success' ? (
                    <svg className="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                ) : (
                    <svg className="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                )}
                <span>{feedback.message}</span>
                </div>
            )}
            
            {/* Contextual Hint */}
            <div className="mt-4 text-xs text-blue-700 bg-blue-100/60 px-3 py-2 rounded-lg border border-blue-200 flex items-start gap-2">
                <span className="text-base leading-none mt-0.5">💡</span>
                <span>{currentConfig.hint}</span>
            </div>
            </div>
        )}

      {/* Settings List */}
      {loading ? <p className="text-center py-8 text-gray-500">Loading...</p> : (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 divide-y divide-gray-100">
          {settings.length === 0 ? (
            <p className="text-center py-8 text-gray-500">No settings in this category yet. Click "Add New" above to create one.</p>
          ) : settings.map(setting => (
            <div key={setting.id} className="flex items-center justify-between px-4 py-3 hover:bg-gray-50">
              <div className="flex items-center gap-3">
                <span className="text-xs font-mono bg-gray-100 px-2 py-1 rounded text-gray-600">{setting.key}</span>
                <span className="text-sm font-medium text-gray-900">{setting.label}</span>
                <span className="text-xs text-gray-400">Order: {setting.sortOrder}</span>
              </div>
              <button 
                onClick={() => handleDeactivate(setting.id)}
                className="text-red-400 hover:text-red-600 hover:bg-red-50 p-2 rounded-lg transition"
                title="Deactivate"
              >
                <XCircle className="h-4 w-4" />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}