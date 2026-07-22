import React, { useState } from 'react';
import { Plus, Building2, Layers, LayoutGrid, ChevronRight } from 'lucide-react';
import { Button, Input, Modal, ErrorBanner, Card, EmptyState } from '../components/ui';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { hostelsService, blocksService, floorsService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

// Three-column drill-down: Hostels -> Blocks (of selected hostel) -> Floors (of selected block).
// This is the missing piece that unblocks Room creation, since every Room needs a Floor,
// every Floor needs a Block, and every Block needs a Hostel.
export const HostelsView = () => {
  const { t } = useLanguage();
  const { addToast } = useToast();
  const [selectedHostel, setSelectedHostel] = useState(null);
  const [selectedBlock, setSelectedBlock] = useState(null);

  const { data: hostels, isLoading: hostelsLoading, error: hostelsError, refetch: refetchHostels } =
    useQuery(() => hostelsService.getAll(), []);
  const { data: blocks, isLoading: blocksLoading, refetch: refetchBlocks } = useQuery(
    () => (selectedHostel ? blocksService.getAll(selectedHostel.id) : Promise.resolve([])),
    [selectedHostel?.id]
  );
  const { data: floors, isLoading: floorsLoading, refetch: refetchFloors } = useQuery(
    () => (selectedBlock ? floorsService.getAll(selectedBlock.id) : Promise.resolve([])),
    [selectedBlock?.id]
  );

  const [hostelModalOpen, setHostelModalOpen] = useState(false);
  const [blockModalOpen, setBlockModalOpen] = useState(false);
  const [floorModalOpen, setFloorModalOpen] = useState(false);
  const [formError, setFormError] = useState('');

  const createHostelMutation = useMutation((payload) => hostelsService.create(payload));
  const createBlockMutation = useMutation((payload) => blocksService.create(payload));
  const createFloorMutation = useMutation((payload) => floorsService.create(payload));

  const handleCreateHostel = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = Object.fromEntries(form.entries());
    if (payload.totalCapacity) payload.totalCapacity = Number(payload.totalCapacity);
    else delete payload.totalCapacity;
    try {
      await createHostelMutation.mutate(payload);
      addToast('Hostel created successfully!');
      setHostelModalOpen(false);
      refetchHostels();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to create hostel', 'error');
    }
  };

  const handleCreateBlock = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = { name: form.get('name'), hostelId: selectedHostel.id };
    try {
      await createBlockMutation.mutate(payload);
      addToast('Block created successfully!');
      setBlockModalOpen(false);
      refetchBlocks();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to create block', 'error');
    }
  };

  const handleCreateFloor = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = { floorNumber: Number(form.get('floorNumber')), blockId: selectedBlock.id };
    try {
      await createFloorMutation.mutate(payload);
      addToast('Floor created successfully!');
      setFloorModalOpen(false);
      refetchFloors();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to create floor', 'error');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_hostels')}</h2>
          <p className="text-sm text-slate-500 mt-1">
            Build the Hostel → Block → Floor hierarchy. Rooms are created against a Floor on the Rooms page.
          </p>
        </div>
      </div>

      <ErrorBanner message={hostelsError} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Hostels column */}
        <Card className="p-5">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2 font-semibold text-slate-800">
              <Building2 className="w-4 h-4 text-indigo-500" /> Hostels
            </div>
            <Button
              variant="secondary"
              className="px-2.5 py-1.5 h-auto text-xs"
              icon={Plus}
              onClick={() => {
                setFormError('');
                setHostelModalOpen(true);
              }}
            >
              {t('add')}
            </Button>
          </div>
          <div className="space-y-1.5">
            {hostelsLoading && <p className="text-sm text-slate-400">Loading…</p>}
            {!hostelsLoading && (!hostels || hostels.length === 0) && (
              <p className="text-sm text-slate-400">No hostels yet — add one to get started.</p>
            )}
            {hostels?.map((h) => (
              <button
                key={h.id}
                onClick={() => {
                  setSelectedHostel(h);
                  setSelectedBlock(null);
                }}
                className={`w-full flex items-center justify-between text-left px-3 py-2.5 rounded-lg text-sm transition-colors ${
                  selectedHostel?.id === h.id ? 'bg-indigo-50 text-indigo-700 font-semibold' : 'hover:bg-slate-50 text-slate-600'
                }`}
              >
                <span>
                  {h.name}
                  <span className="block text-xs font-normal text-slate-400">
                    {h.blockCount} block{h.blockCount === 1 ? '' : 's'} · id {h.id}
                  </span>
                  <span className="block text-xs font-normal text-slate-400">
                    Warden: {h.wardenName || 'Unassigned — set one on the Wardens page'}
                  </span>
                </span>
                <ChevronRight className="w-4 h-4 shrink-0" />
              </button>
            ))}
          </div>
        </Card>

        {/* Blocks column */}
        <Card className="p-5">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2 font-semibold text-slate-800">
              <LayoutGrid className="w-4 h-4 text-indigo-500" /> Blocks
            </div>
            <Button
              variant="secondary"
              className="px-2.5 py-1.5 h-auto text-xs"
              icon={Plus}
              disabled={!selectedHostel}
              onClick={() => {
                setFormError('');
                setBlockModalOpen(true);
              }}
            >
              {t('add')}
            </Button>
          </div>
          {!selectedHostel ? (
            <p className="text-sm text-slate-400">Select a hostel to view its blocks.</p>
          ) : (
            <div className="space-y-1.5">
              {blocksLoading && <p className="text-sm text-slate-400">Loading…</p>}
              {!blocksLoading && (!blocks || blocks.length === 0) && (
                <p className="text-sm text-slate-400">No blocks yet in {selectedHostel.name}.</p>
              )}
              {blocks?.map((b) => (
                <button
                  key={b.id}
                  onClick={() => setSelectedBlock(b)}
                  className={`w-full flex items-center justify-between text-left px-3 py-2.5 rounded-lg text-sm transition-colors ${
                    selectedBlock?.id === b.id ? 'bg-indigo-50 text-indigo-700 font-semibold' : 'hover:bg-slate-50 text-slate-600'
                  }`}
                >
                  <span>
                    {b.name}
                    <span className="block text-xs font-normal text-slate-400">
                      {b.floorCount} floor{b.floorCount === 1 ? '' : 's'} · id {b.id}
                    </span>
                  </span>
                  <ChevronRight className="w-4 h-4 shrink-0" />
                </button>
              ))}
            </div>
          )}
        </Card>

        {/* Floors column */}
        <Card className="p-5">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2 font-semibold text-slate-800">
              <Layers className="w-4 h-4 text-indigo-500" /> Floors
            </div>
            <Button
              variant="secondary"
              className="px-2.5 py-1.5 h-auto text-xs"
              icon={Plus}
              disabled={!selectedBlock}
              onClick={() => {
                setFormError('');
                setFloorModalOpen(true);
              }}
            >
              {t('add')}
            </Button>
          </div>
          {!selectedBlock ? (
            <p className="text-sm text-slate-400">Select a block to view its floors.</p>
          ) : (
            <div className="space-y-1.5">
              {floorsLoading && <p className="text-sm text-slate-400">Loading…</p>}
              {!floorsLoading && (!floors || floors.length === 0) && (
                <p className="text-sm text-slate-400">No floors yet in {selectedBlock.name}.</p>
              )}
              {floors?.map((f) => (
                <div key={f.id} className="px-3 py-2.5 rounded-lg text-sm bg-slate-50 text-slate-600">
                  Floor {f.floorNumber}
                  <span className="block text-xs font-normal text-slate-400">
                    {f.roomCount} room{f.roomCount === 1 ? '' : 's'} · id {f.id} — use this id on the Rooms page
                  </span>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {(!hostels || hostels.length === 0) && !hostelsLoading && (
        <EmptyState
          icon={Building2}
          title="No hostels set up yet"
          description="Create a Hostel, then a Block inside it, then a Floor inside that block — that unlocks room creation."
        />
      )}

      {hostelModalOpen && (
        <Modal title="Add New Hostel" onClose={() => setHostelModalOpen(false)}>
          <form onSubmit={handleCreateHostel} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <Input label="Hostel Name" name="name" required placeholder="e.g. Sunrise Hostel" />
            <Input label="Address" name="address" placeholder="e.g. 12 College Road" />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input label="Type" name="type" placeholder="BOYS / GIRLS / CO_ED" />
              <Input label="Total Capacity" name="totalCapacity" type="number" min="1" placeholder="e.g. 200" />
            </div>
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setHostelModalOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createHostelMutation.isPending}>
                {t('save')}
              </Button>
            </div>
          </form>
        </Modal>
      )}

      {blockModalOpen && (
        <Modal title={`Add Block to ${selectedHostel?.name}`} onClose={() => setBlockModalOpen(false)}>
          <form onSubmit={handleCreateBlock} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <Input label="Block Name" name="name" required placeholder="e.g. Block A" />
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setBlockModalOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createBlockMutation.isPending}>
                {t('save')}
              </Button>
            </div>
          </form>
        </Modal>
      )}

      {floorModalOpen && (
        <Modal title={`Add Floor to ${selectedBlock?.name}`} onClose={() => setFloorModalOpen(false)}>
          <form onSubmit={handleCreateFloor} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <Input label="Floor Number" name="floorNumber" type="number" required placeholder="e.g. 1" />
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setFloorModalOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createFloorMutation.isPending}>
                {t('save')}
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
